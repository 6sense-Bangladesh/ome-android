package com.ome.app.presentation.dashboard.my_stove.device

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceSettingsBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManualFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceSettingsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceSettingsBinding>(FragmentDeviceSettingsBinding::inflate) {

    override val viewModel: DeviceViewModel by viewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()
    val params by lazy { args.params }

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun setupUI() {
        binding.apply {
            viewModel.stovePosition = mainViewModel.getStovePositionByMac(viewModel.macAddress)
//            name.text = params.name
            mainViewModel.knobs.value.find { it.macAddr == params.macAddr }?.let { knob ->
//                knobView.setFontSize(18F)
            }
            recyclerView.adapter = adapter
            knobTv.text = getString(R.string.knob_, viewModel.stovePosition)
            macAddressTv.text = getString(R.string.knob_mac_addr_label, params.macAddr)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.macAddress = params.macAddr
        viewModel.initSubscriptions()
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
    }


    override fun setupObserver() {
        super.setupObserver()
        viewModel.currentKnob.collectWithLifecycle {
            it.log("currentKnob")
            binding.knobView.changeKnobBasicStatus(it)
        }
        viewModel.knobAngle.collectWithLifecycle { angle ->
            binding.knobView.setKnobPosition(angle)
            if (viewModel.currentKnob.value?.safetyLock.isTrue()){
                viewModel.currentKnob.value?.calibration?.offAngle?.toFloat()?.let {
                    binding.knobView.setKnobPosition(it)
                }
            }
        }

        viewModel.isDualZone = mainViewModel.getKnobByMac(params.macAddr)?.calibration?.rotationDir == 2
        viewModel.deviceSettingsList.collectWithLifecycle{
            adapter.submitList(it)
        }

        viewModel.loadingFlow.collectWithLifecycle {
            binding.loadingLayout.root.changeVisibility(it)
            if(!it) popBackSafe(R.id.dashboardFragment, false)
        }
    }

    private val onClick: (ItemModel) -> Unit = { item ->
        when (item) {
            is DeviceSettingsItemModel -> {
                when (item) {
                    DeviceSettingsItemModel.KnobPosition -> {
                        navigateSafe(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToSelectBurnerFragment(
                                SelectBurnerFragmentParams(isEditMode = true, macAddress = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.KnobWiFI -> {
                        navigateSafe(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToConnectToWifiFragment(
                                ConnectToWifiParams(isEditMode = true, macAddrs = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.KnobCalibration -> {
                        navigateSafe(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToKnobInstallationManualFragment(
                                KnobInstallationManualFragmentParams(
                                    isComeFromSettings = true,
                                    macAddr = viewModel.macAddress
                                )

                            )
                        )
                    }
                    DeviceSettingsItemModel.DeleteKnob -> {
                        showDialog(
                            message = SpannableStringBuilder(getString(R.string.confirm_delete)),
                            positiveButtonText = getString(R.string.delete),
                            isRedPositiveButton = true,
                            onPositiveButtonClick = {
                                viewModel.deleteKnob()
                            }
                        )
                    }
                }
            }
        }
    }
}
