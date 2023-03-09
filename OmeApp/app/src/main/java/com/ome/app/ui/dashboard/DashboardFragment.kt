package com.ome.app.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentDashboardBinding
import com.ome.app.ui.base.BaseHostFragment
import com.ome.app.ui.base.navigation.BottomNavigationController
import com.ome.app.ui.views.BottomItem
import com.ome.app.utils.getCurrentFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class DashboardFragment :
    BaseHostFragment<DashboardViewModel, FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private lateinit var bottomNavigationController: BottomNavigationController

    override val viewModel: DashboardViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigationController = BottomNavigationController(
            bottomGraphs = listOf(
                BottomNavigationController.BottomGraph(
                    BottomItem.SETTINGS,
                    R.navigation.settings_navigation,
                    R.id.settingsNavigation
                ),
                BottomNavigationController.BottomGraph(
                    BottomItem.MY_STOVE,
                    R.navigation.my_stove_navigation,
                    R.id.myStoveNavigation,
                    if (viewModel.isStoveInfoExist()) R.id.myStoveFragment else R.id.welcomeFragment
                ),
                BottomNavigationController.BottomGraph(
                    BottomItem.MEMBERS,
                    R.navigation.members_navigation,
                    R.id.membersNavigation
                ),
                BottomNavigationController.BottomGraph(
                    BottomItem.PROFILE,
                    R.navigation.profile_navigation,
                    R.id.profileNavigation
                )
            ),
            fragmentManager = childFragmentManager,
            containerId = R.id.dashboardContainer
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigation.applyInsetter {
            type(navigationBars = true) {
                margin(bottom = true)
            }
        }
        initBottomNavigation()
    }


    private fun initBottomNavigation() {
        viewModel.connectBottomNavController(
            bottomNavigationController.setup(
                bottomNavigationView = binding.bottomNavigation,
                intent = requireActivity().intent
            )
        )
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.bottomBarVisible) {
            binding.bottomNavigation.isVisible = it
        }
        subscribe(viewModel.signOutLiveData) {
            findNavController().navigate(R.id.action_dashboardFragment_to_launchFragment)
        }
        subscribe(viewModel.stoveExistLiveData) {
            if (it) {
                binding.bottomNavigation.setEnabledTabState(BottomItem.SETTINGS, true)
                binding.bottomNavigation.setEnabledTabState(BottomItem.MEMBERS, true)
            } else {
                binding.bottomNavigation.setEnabledTabState(BottomItem.SETTINGS, false)
                binding.bottomNavigation.setEnabledTabState(BottomItem.MEMBERS, false)
            }
        }
    }

    override fun getCurrentFragment(): Fragment? =
        (childFragmentManager.findFragmentById(R.id.dashboardContainer) as? NavHostFragment)?.getCurrentFragment()
}
