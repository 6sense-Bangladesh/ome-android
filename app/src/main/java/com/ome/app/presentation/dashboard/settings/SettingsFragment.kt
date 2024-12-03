package com.ome.app.presentation.dashboard.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.DashboardFragmentDirections
import com.ome.app.presentation.dashboard.my_stove.device.DeviceSettingsFragmentParams
import com.ome.app.presentation.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsKnobItemModel
import com.ome.app.presentation.dashboard.settings.add_knob.wake_up.KnobWakeUpParams
import com.ome.app.presentation.stove.StoveSetupBrandArgs
import com.ome.app.presentation.stove.StoveSetupBurnersArgs
import com.ome.app.presentation.stove.StoveSetupTypeArgs
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.isTrue
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<SettingsViewModel, FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override val viewModel: SettingsViewModel by viewModels()

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
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
                        Settings.ADD_NEW_KNOB ->{
                            if(mainViewModel.userInfo.value.stoveSetupComplete.isTrue())
                                parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToKnobWakeUpFragment(KnobWakeUpParams()))
                            else 
                                toast("Complete stove setup first.")
                        }
                        Settings.STOVE_BRAND ->
                            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupBrandFragment(StoveSetupBrandArgs(isEditMode = true)))
                        Settings.STOVE_TYPE ->
                            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupTypeFragment(StoveSetupTypeArgs(isEditMode = true)))
                        Settings.STOVE_LAYOUT ->
                            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToStoveSetupBurnersFragment(StoveSetupBurnersArgs(isEditMode = true)))
                        Settings.STOVE_AUTO_SHUT_OFF ->
                            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToAutoShutOffSettingsFragment())
                    }
                }
            }

            is SettingsKnobItemModel -> {
                parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToDeviceSettingsFragment(DeviceSettingsFragmentParams(macAddr = item.macAddr)))
            }
        }
    }

    override fun handleBackPressEvent() {}

}
