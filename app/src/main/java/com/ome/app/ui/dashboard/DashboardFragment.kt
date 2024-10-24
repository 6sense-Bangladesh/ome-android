package com.ome.app.ui.dashboard

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ome.app.R
import com.ome.app.databinding.FragmentDashboardBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.my_stove.MyStoveFragment
import com.ome.app.ui.dashboard.profile.ProfileFragment
import com.ome.app.ui.dashboard.settings.SettingsFragment
import com.ome.app.ui.views.ViewPagerAdapter
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressedIgnoreCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

//    private lateinit var bottomNavigationController: BottomNavigationController

    override val viewModel: DashboardViewModel by viewModels()

    private val navFragments= listOf(
        SettingsFragment(),
        MyStoveFragment(),
        ProfileFragment()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        bottomNavigationController = BottomNavigationController(
//            bottomGraphs = listOf(
//                BottomNavigationController.BottomGraph(
//                    BottomItem.SETTINGS,
//                    R.navigation.settings_navigation,
//                    R.id.settingsNavigation
//                ),
//                BottomNavigationController.BottomGraph(
//                    BottomItem.MY_STOVE,
//                    R.navigation.my_stove_nav_graph,
//                    R.id.myStoveSetupNavGraph,
//                    if (viewModel.isStoveInfoExist()) R.id.myStoveFragment else R.id.welcomeFragment
//                ),
//                BottomNavigationController.BottomGraph(
//                    BottomItem.MEMBERS,
//                    R.navigation.members_navigation,
//                    R.id.membersNavigation
//                ),
//                BottomNavigationController.BottomGraph(
//                    BottomItem.PROFILE,
//                    R.navigation.profile_navigation,
//                    R.id.profileNavigation
//                )
//            ),
//            fragmentManager = childFragmentManager,
//            containerId = R.id.dashboardContainer
//        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.bottomNavigation.applyInsetter {
//            type(navigationBars = true) {
//                margin(top = true, bottom = true)
//            }
//        }
        initBottomNavigation()
    }

    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            if (binding.dashboardViewPager.currentItem != 1)
                binding.dashboardViewPager.currentItem = 1
            else
                onBackPressedIgnoreCallback()
        }
    }

    override fun setupUI() {
        when(binding.dashboardViewPager.currentItem){
            0 -> binding.topAppBar.title = getString(R.string.menu_settings)
            1 -> binding.topAppBar.title = getString(R.string.app_name)
            2 -> {
                binding.topAppBar.title = getString(R.string.menu_profile)
                binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, true)
            }
        }
    }


    private fun initBottomNavigation() {
        binding.dashboardViewPager.adapter = ViewPagerAdapter(navFragments,this)
        binding.bottomNavigation.selectedItemId = R.id.menuMyStove
        binding.dashboardViewPager.setCurrentItem(1, false)
//        binding.root.post {
//            binding.dashboardViewPager.setCurrentItem(1, false)
//        }
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuLogout -> {
                    showDialog(
                        message = SpannableStringBuilder(getString(R.string.confirm_logout)),
                        positiveButtonText = getString(R.string.logout),
                        isRedPositiveButton = true,
                        onPositiveButtonClick = {
                            mainViewModel.signOut(onEnd = {
                                navigateSafe(R.id.action_dashboardFragment_to_launchFragment) ?: activity?.finish()
                            })
                        })
                    true
                }
                R.id.menuFeedback ->{
                    navigateSafe(R.id.action_dashboardFragment_to_supportFragment)
                    true
                }
                else -> false
            }
        }
        binding.dashboardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> {
                        binding.topAppBar.title = getString(R.string.menu_settings)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, false)
                        binding.bottomNavigation.selectedItemId = R.id.menuSettings
                    }
                    1 -> {
                        binding.topAppBar.title = getString(R.string.app_name)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, false)
                        binding.bottomNavigation.selectedItemId = R.id.menuMyStove
                    }
                    2 -> {
                        binding.topAppBar.title = getString(R.string.menu_profile)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, true)
                        binding.bottomNavigation.selectedItemId = R.id.menuProfile
                    }
                }
            }
        })
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menuSettings -> {
                    binding.dashboardViewPager.currentItem = 0
                    true
                }
                R.id.menuMyStove -> {
                    binding.dashboardViewPager.currentItem = 1
                    true
                }
                R.id.menuProfile -> {
                    binding.dashboardViewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
//        viewModel.connectBottomNavController(
//            bottomNavigationController.setup(
//                bottomNavigationView = binding.bottomNavigation,
//                intent = requireActivity().intent
//            )
//        )
    }

    override fun setupObserver() {
        super.setupObserver()
//        subscribe(viewModel.bottomBarVisible) {
//            binding.bottomNavigation.isVisible = it
//        }
//        subscribe(viewModel.signOutLiveData) {
//            findNavController().navigate(R.id.action_dashboardFragment_to_launchFragment)
//        }
//        subscribe(viewModel.stoveExistLiveData) {
//            if (it) {
//                binding.bottomNavigation.setEnabledTabState(BottomItem.SETTINGS, true)
//                binding.bottomNavigation.setEnabledTabState(BottomItem.MEMBERS, true)
//            } else {
//                binding.bottomNavigation.setEnabledTabState(BottomItem.SETTINGS, false)
//                binding.bottomNavigation.setEnabledTabState(BottomItem.MEMBERS, false)
//            }
//        }
    }
}
