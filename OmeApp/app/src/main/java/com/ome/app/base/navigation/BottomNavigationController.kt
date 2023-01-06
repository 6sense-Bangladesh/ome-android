package com.ome.app.base.navigation

import android.content.Intent
import android.util.SparseArray
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.core.util.forEach
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.ui.views.BottomItem
import com.ome.app.ui.views.BottomNavbar
import com.ome.app.utils.logd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BottomNavigationController(
    private val bottomGraphs: List<BottomGraph>,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    data class BottomGraph(
        val bottomNavigationAction: BottomItem,
        @NavigationRes
        val graphResId: Int,
        @IdRes
        val graphId: Int,
        @IdRes
        val startDestinationId: Int? = null
    )

    /**
     * Interface proposed to be used by root screens of each tab
     *  to inform that screen was restored (so u may update displayed data etc)
     */
    interface UpdatableOnRestore {
        fun onRestoreFromTab()
    }

    private val menuIdToTagMap = SparseArray<String>()
    private val tagToOpenedAtFirstMap = hashMapOf<String, Boolean>()

    private var selectedItemTag: String? = null

    var onReselectedListener: (() -> Unit)? = null

    // todo handle deeplink intent in future
    fun setup(
        bottomNavigationView: BottomNavbar,
        intent: Intent?
    ): StateFlow<NavController> {

        lateinit var selectedController: MutableStateFlow<NavController>

        if (fragmentManager.findFragmentById(containerId) == null) { // fragment manager is empty, configuring nav hosts;
            bottomGraphs
                .sortedBy {
                    // Do not touch this ordering, there is a crash came from NavHostFragment's onDestroyView
                    // says that navController is null. That's why we're adding not selected items first and selected one as the last;
                    it.bottomNavigationAction == bottomNavigationView.currentActiveTab
                }
                .forEach { bottomGraph ->
                    val fragmentTag = getNavFragmentTag(bottomGraph.bottomNavigationAction.ordinal)

                    val navHostFragment = obtainNavHostFragment(
                        bottomGraph.graphResId,
                        fragmentTag,
                        fragmentManager,
                        containerId
                    )
                    menuIdToTagMap[bottomGraph.bottomNavigationAction.ordinal] = fragmentTag

                    if (bottomNavigationView.currentActiveTab.ordinal == bottomGraph.bottomNavigationAction.ordinal) {
                        // Restoring selected one
                        selectedController = MutableStateFlow(navHostFragment.navController)

                        bottomGraph.startDestinationId?.let {
                            val inflater = selectedController.value.navInflater
                            val graph = inflater.inflate(R.navigation.my_stove_navigation)
                            graph.setStartDestination(it)
                            selectedController.value.setGraph(graph, null)
                        }

                        selectedItemTag = fragmentTag

                        fragmentManager
                            .beginTransaction()
                            .attach(navHostFragment)
                            .commitNow()
                        tagToOpenedAtFirstMap[fragmentTag] = true
                    } else {
                        fragmentManager
                            .beginTransaction()
                            .detach(navHostFragment)
                            .commitNow()

                        tagToOpenedAtFirstMap[fragmentTag] = false
                    }
                }
        } else {
            // Restore menuIdToTag ma
            val currentFragmentGraph =
                (fragmentManager.findFragmentById(containerId) as NavHostFragment)
                    .findNavController()
                    .graph
                    .id

            bottomGraphs
                .sortedBy {
                    // Do not touch this ordering, there is a crash came from NavHostFragment's onDestroyView
                    // says that navController is null. That's why we're adding not selected items first and selected one as the last;
                    it.bottomNavigationAction.ordinal == bottomNavigationView.currentActiveTab.ordinal
                }
                .forEach { bottomGraph ->
                    val fragmentTag = getNavFragmentTag(bottomGraph.bottomNavigationAction.ordinal)
                    menuIdToTagMap[bottomGraph.bottomNavigationAction.ordinal] = fragmentTag

                    if (currentFragmentGraph == bottomGraph.graphId) {
                        selectedController = MutableStateFlow(
                            obtainNavHostFragment(
                                bottomGraph.graphResId,
                                fragmentTag,
                                fragmentManager,
                                containerId
                            ).navController
                        )

                        tagToOpenedAtFirstMap[fragmentTag] = true
                    } else {
                        tagToOpenedAtFirstMap[fragmentTag] = false
                    }
                }
        }

        bottomNavigationView.destinationListener = { action, commitNow ->
            logd("onActionSelected $action")
            if (menuIdToTagMap[action.ordinal] == selectedItemTag) {
                // Reselect
            } else {

                val selectedTag = menuIdToTagMap[action.ordinal]
                logd("selectedTag $selectedTag")

                val selectedFragment =
                    fragmentManager.findFragmentByTag(selectedTag) as NavHostFragment
                logd("selectedFragment $selectedFragment")
                val transaction = fragmentManager
                    .beginTransaction()
                    .attach(selectedFragment)
                    .setPrimaryNavigationFragment(selectedFragment)
                    .apply {
                        menuIdToTagMap.forEach { _, tag ->
                            if (tag != selectedTag) {
                                detach(fragmentManager.findFragmentByTag(tag)!!)
                            }
                        }
                    }
                    .setReorderingAllowed(true)

                if (commitNow) {
                    transaction.commitNow()
                } else {
                    transaction.commit()
                }

                selectedItemTag = selectedTag
                logd("selectedItemTag $selectedTag")
                selectedController.value = selectedFragment.findNavController()
                logd("selectedController ${selectedFragment.findNavController()}")

                val childFragment = selectedFragment.childFragmentManager.primaryNavigationFragment
                if (childFragment is UpdatableOnRestore && tagToOpenedAtFirstMap[selectedTag] == true) {
                    childFragment.onRestoreFromTab()
                }

                tagToOpenedAtFirstMap[selectedTag] = true
            }
        }

        return selectedController
    }

    private fun getNavFragmentTag(menuId: Int) = "bottomNavigationController#$menuId"

    // Find fragment by tag or creates a new one
    private fun obtainNavHostFragment(
        graphResId: Int,
        fragmentTag: String,
        fragmentManager: FragmentManager,
        containerId: Int
    ): NavHostFragment {
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?

        return existingFragment ?: NavHostFragment.create(graphResId).let { navHostFragment ->
            fragmentManager
                .beginTransaction()
                .add(containerId, navHostFragment, fragmentTag)
                .commitNow()

            navHostFragment
        }
    }
}
