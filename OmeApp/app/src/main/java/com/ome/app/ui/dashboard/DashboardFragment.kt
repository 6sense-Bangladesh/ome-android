package com.ome.app.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentDashboardBinding
import com.ome.app.base.BaseFragment
import com.ome.app.base.navigation.BottomNavigationController
import com.ome.app.ui.views.BottomItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private lateinit var bottomNavigationController: BottomNavigationController

    override val viewModel: DashboardViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//           if (viewModel.isStoveInfoExist()) R.id.myStoveFragment else R.id.welcomeFragment


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
                    R.id.welcomeFragment
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
        //           if (viewModel.isStoveInfoExist()) R.id.myStoveFragment else R.id.welcomeFragment
        binding.bottomNavigation.setEnabledTabState(BottomItem.SETTINGS, false)
        binding.bottomNavigation.setEnabledTabState(BottomItem.MEMBERS, false)
        binding.bottomNavigation.setEnabledTabState(BottomItem.PROFILE, false)
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
    }

}
