package com.ome.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.ome.app.utils.logd


abstract class BaseHostFragment<VM : BaseViewModel, VB : ViewBinding>(
    factory: (LayoutInflater) -> VB
) : BaseFragment<VM,VB>(factory) {

//    override val navigationCommandHandler: NavigationCommandHandler = NavigationCommandHandler(
//        navControllerDefinition = { findNavController() },
//        childNavControllerDefinition = { getCurrentFragment()!!.findNavController() }
//    )


    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, _ ->
            logd("Destination changed in inner graph, graphId: ${controller.graph.id}, destinationId: $destination")
            viewModel.currentDestination.value = destination
            mainViewModel.currentDestination.value = destination
        }

    private val fragmentLifecycleListener = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            if (f is NavHostFragment) {
                f.navController.addOnDestinationChangedListener(destinationChangedListener)
            }
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            if (f is NavHostFragment) {
                f.navController.removeOnDestinationChangedListener(destinationChangedListener)
            }
        }
    }

    abstract fun getCurrentFragment(): Fragment?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleListener, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleListener)
    }

}
