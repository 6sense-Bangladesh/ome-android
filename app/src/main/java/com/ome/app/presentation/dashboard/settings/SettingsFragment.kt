package com.ome.app.presentation.dashboard.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.R
import com.ome.app.databinding.FragmentSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.DashboardFragmentDirections
import com.ome.app.presentation.dashboard.my_stove.device.DeviceSettingsFragmentParams
import com.ome.app.presentation.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.presentation.dashboard.settings.adapter.StovesBottomSheet
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsKnobItemModel
import com.ome.app.presentation.dashboard.settings.add_knob.wake_up.KnobWakeUpParams
import com.ome.app.presentation.stove.StoveSetupBrandArgs
import com.ome.app.presentation.stove.StoveSetupBurnersArgs
import com.ome.app.presentation.stove.StoveSetupTypeArgs
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
//        binding.recyclerView.addItemDecoration(
//            SettingsItemDecoration()
//        )
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
                        Settings.ADD_NEW_KNOB ->
                            navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToKnobWakeUpFragment(KnobWakeUpParams()))
                        Settings.STOVE_BRAND ->
                            navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupBrandFragment(StoveSetupBrandArgs(isEditMode = true)))
                        Settings.STOVE_TYPE ->
                            navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupTypeFragment(StoveSetupTypeArgs(isEditMode = true)))
                        Settings.STOVE_LAYOUT ->
                            navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupBurnersFragment(StoveSetupBurnersArgs(isEditMode = true)))
                        Settings.STOVE_AUTO_SHUT_OFF ->
                            navController?.navigate(DashboardFragmentDirections.actionDashboardFragmentToAutoShutOffSettingsFragment())
                    }
                }
            }

            is SettingsKnobItemModel -> {
                navController?.navigate(
                    DashboardFragmentDirections.actionDashboardFragmentToDeviceSettingsFragment(
                        DeviceSettingsFragmentParams(
                            stovePosition = item.stovePosition,
                            macAddr = item.macAddr
                        )
                    )
                )
            }
        }
    }

    override fun handleBackPressEvent() {}

}
