package com.ome.app.presentation.dashboard

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ome.app.R
import com.ome.app.databinding.FragmentDashboardBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.my_stove.MyStoveFragment
import com.ome.app.presentation.dashboard.profile.ProfileFragment
import com.ome.app.presentation.dashboard.settings.SettingsFragment
import com.ome.app.presentation.views.ViewPagerAdapter
import com.ome.app.utils.changeVisibility
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressedIgnoreCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    override val viewModel: DashboardViewModel by viewModels()

    private val navFragments = listOf(
        SettingsFragment(),
        MyStoveFragment(),
        ProfileFragment()
    )

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Directly ask for the permission
                requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun setupObserver() {
        super.setupObserver()
        val menuItem = binding.topAppBar.menu?.findItem(R.id.menuLock)
        mainViewModel.knobState.collectWithLifecycle { state ->
            if (binding.dashboardViewPager.currentItem != 1) return@collectWithLifecycle
            if (state.toList().any { it.second.knobSetSafetyMode == true }) {
                menuItem?.setIcon(R.drawable.ic_safety_lock)
            } else {
                menuItem?.setIcon(R.drawable.ic_safety_lock_open)
            }
        }
        mainViewModel.loadingFlow.collectWithLifecycle {
            binding.loadingLayout.root.changeVisibility(it)
        }
    }

    override fun setupUI() {
        askNotificationPermission()
        when (binding.dashboardViewPager.currentItem) {
            0 -> binding.topAppBar.title = getString(R.string.menu_settings)
            1 -> {
                binding.topAppBar.title = getString(R.string.app_name)
                binding.topAppBar.menu?.setGroupVisible(R.id.group_my_stove, true)
            }

            2 -> {
                binding.topAppBar.title = getString(R.string.menu_profile)
                binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, true)
            }
        }
    }


    private fun initBottomNavigation() {
        binding.dashboardViewPager.adapter = ViewPagerAdapter(navFragments, this)
        binding.bottomNavigation.selectedItemId = R.id.menuMyStove
        binding.dashboardViewPager.setCurrentItem(1, false)
        binding.topAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menuLogout -> {
                    showDialog(
                        message = SpannableStringBuilder(getString(R.string.confirm_logout)),
                        positiveButtonText = getString(R.string.logout),
                        isRedPositiveButton = true,
                        onPositiveButtonClick = {
                            mainViewModel.signOut(onEnd = {
                                navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToLaunchFragment())
                                    ?: activity?.finish()
                            })
                        })
                    true
                }

                R.id.menuFeedback -> {
                    navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToSupportFragment())
                    true
                }

                R.id.menuLock -> {
                    mainViewModel.knobState.value.toList()
                        .any { it.second.knobSetSafetyMode == true }.also {
                        showDialog(
                            title = if (it) getString(R.string.disable_safety_lock) else getString(R.string.enable_safety_lock),
                            onPositiveButtonClick = {
                                if (it) mainViewModel.setSafetyLockOff() else mainViewModel.setSafetyLockOn()
                            },
                            message = SpannableStringBuilder(
                                if (it) getString(R.string.confirm_disable_safety_lock)
                                else getString(R.string.confirm_enable_safety_lock)
                            ),
                            positiveButtonText = if (it) getString(R.string.yes_disable) else getString(
                                R.string.yes_enable
                            ),
                            negativeButtonText = getString(R.string.no_btn)
                        )
                    }
                    true
                }

                else -> false
            }
        }
        binding.dashboardViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.topAppBar.title = getString(R.string.menu_settings)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, false)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_my_stove, false)
                        binding.bottomNavigation.selectedItemId = R.id.menuSettings
                    }

                    1 -> {
                        binding.topAppBar.title = getString(R.string.app_name)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, false)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_my_stove, true)
                        binding.bottomNavigation.selectedItemId = R.id.menuMyStove
                    }

                    2 -> {
                        binding.topAppBar.title = getString(R.string.menu_profile)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_my_stove, false)
                        binding.topAppBar.menu?.setGroupVisible(R.id.group_profile, true)
                        binding.bottomNavigation.selectedItemId = R.id.menuProfile
                    }
                }
            }
        })
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
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
    }

}
