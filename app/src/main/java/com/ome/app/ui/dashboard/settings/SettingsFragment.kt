package com.ome.app.ui.dashboard.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.R
import com.ome.app.databinding.FragmentSettingsBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.navigate
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.DashboardFragmentDirections
import com.ome.app.ui.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.ui.dashboard.settings.adapter.SettingsItemDecoration
import com.ome.app.ui.dashboard.settings.adapter.StovesBottomSheet
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsKnobItemModel
import com.ome.app.ui.dashboard.settings.device.DeviceSettingsFragmentParams
import com.ome.app.utils.collectWithLifecycle
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<SettingsViewModel, FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override val viewModel: SettingsViewModel by viewModels()
    private var navController: NavController? = null

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.navHost) as? NavHostFragment
        navController = navHostFragment?.navController
//        binding.titleTv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
//        binding.rootCl.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(bottom = true)
//            }
//        }
//        binding.supportIv.setOnClickListener {
//            navController?.navigate(R.id.action_dashboardFragment_to_supportFragment)
//        }

//        binding.stoveSubtitleCl.setOnClickListener { showStoves() }
        viewModel.loadSettings()
    }


    private fun showStoves() {
        val dialog = StovesBottomSheet()
        dialog.onStoveClick = {}
        dialog.show(parentFragmentManager, "stove_bottom_sheet")
    }


    override fun setupUI() {
        binding.recyclerView.addItemDecoration(
            SettingsItemDecoration()
        )
        binding.recyclerView.adapter = adapter
//        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.settingsList.collectWithLifecycle {
            adapter.submitList(it)
        }
    }

    private val onClick: (ItemModel) -> Unit = { item ->
        when (item) {
            is SettingsItemModel -> {
                val foundValue = Settings.entries.firstOrNull { it.option == item.option }
                foundValue?.let { option ->
                    when (option) {
//                                Settings.LEAVE_STOVE -> {
//                                    val str =
//                                        SpannableStringBuilder("Please Confirm that you would like to LEAVE “Family #1 Stove”?")
//                                    val first = str.indexOf("LEAVE")
//                                    str.setSpan(
//                                        ForegroundColorSpan(Color.RED),
//                                        first,
//                                        first + 5,
//                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                                    )
//                                    showDialog(
//                                        message = str,
//                                        positiveButtonText = "Accept",
//                                        negativeButtonText = "Reject",
//                                        onPositiveButtonClick = {},
//                                        onNegativeButtonClick = {}
//                                    )
//                                }
//                                Settings.STOVE_INFO_SETTINGS -> {
//                                    navController.navigate(Screens.StoveInfo)
//                                    navController?.navigate(
//                                        SettingsFragmentDirections.actionSettingsFragmentToStoveInfoFragment(
//                                            viewModel.userRepository.userFlow.value?.stoveId ?: ""
//                                        )
//                                    )
//                                }
//                                Settings.STOVE_HISTORY -> {
//
//                                }
                        Settings.ADD_NEW_KNOB -> {
                            navController?.navigate(R.id.action_dashboardFragment_to_addKnobNavGraph)
//                                    navController.navigate(NavGraph.AddKnob)
                            //navController?.navigate(R.id.addKnobNavGraph)
                            //navController?.navigate(SettingsFragmentDirections.actionSettingsFragmentToKnobWakeUpFragment(isComeFromSettings = false))
                        }

                        Settings.STOVE_BRAND -> Screens.StoveBrand.navigate(navController)
                        Settings.STOVE_TYPE -> Screens.StoveType.navigate(navController)
                        Settings.STOVE_LAYOUT -> Screens.StoveLayout.navigate(navController)
                        Settings.STOVE_AUTO_SHUT_OFF ->
                            navController?.navigate(R.id.action_dashboardFragment_to_autoShutOffSettingsFragment)
                    }
                }
            }

            is SettingsKnobItemModel -> {
                navController?.navigate(
                    DashboardFragmentDirections.actionDashboardFragmentToDeviceSettingsFragment(
                        DeviceSettingsFragmentParams(
                            name = item.name,
                            macAddr = item.macAddr
                        )
                    )
                )
            }
        }
    }

    override fun handleBackPressEvent() {}

}
