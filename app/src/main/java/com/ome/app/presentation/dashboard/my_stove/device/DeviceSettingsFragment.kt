package com.ome.app.presentation.dashboard.my_stove.device

import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceSettingsBinding
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.state.Rotation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.settings.adapter.SettingItemAdapter
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.presentation.views.KnobView
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DeviceSettingsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceSettingsBinding>(FragmentDeviceSettingsBinding::inflate) {

    override val viewModel: DeviceViewModel by viewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()

    private val adapter by lazy { SettingItemAdapter(onClick) }

    override fun setupUI() {
        binding.apply {
            viewModel.macAddress = args.params.macAddr
            viewModel.stovePosition = mainViewModel.getStovePositionByMac(viewModel.macAddress)
//            name.text = args.params.name
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let { knob ->
                knobView.changeKnobBasicStatus(knob)
//                knobView.setFontSize(18F)
            }
            recyclerView.adapter = adapter
            viewModel.initSubscriptions()
            knobTv.text = getString(R.string.knob_, viewModel.stovePosition)
            macAddressTv.text = getString(R.string.knob_mac_addr_label, args.params.macAddr)
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
    }


    override fun setupObserver() {
        super.setupObserver()
//        viewModel.webSocketManager.knobAngleFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {angle ->
//                binding.knobView.setKnobPosition(angle.value.toFloat())
//            }
        viewModel.currentKnob.collectWithLifecycle {
            it.log("currentKnob")
            binding.knobView.setupKnob(it)
        }
//        viewModel.zonesLiveData.collectWithLifecycle{
//            binding.knobView.setOffPosition(it.offAngle.toFloat())
//            if (it.rotation == Rotation.DUAL) {
//                if (it.zones1 != null) {
//                    binding.knobView.setLowSinglePosition(it.zones1.lowAngle.toFloat())
//                    binding.knobView.setHighSinglePosition(it.zones1.highAngle.toFloat())
//                } else if(it.zones2 != null){
//                    binding.knobView.setLowDualPosition(it.zones2.lowAngle.toFloat())
//                    binding.knobView.setHighDualPosition(it.zones2.highAngle.toFloat())
//                }
//            } else if (it.zones1 != null) {
//                binding.knobView.setOffPosition(it.offAngle.toFloat())
//                binding.knobView.setLowSinglePosition(it.zones1.lowAngle.toFloat())
//                binding.knobView.setMediumPosition(it.zones1.mediumAngle.toFloat())
//                binding.knobView.setHighSinglePosition(it.zones1.highAngle.toFloat())
//            }
//        }
        viewModel.knobAngle.collectWithLifecycle { angle ->
            binding.knobView.setKnobPosition(angle)
        }

        viewModel.isDualZone = mainViewModel.getKnobByMac(args.params.macAddr)?.calibration?.rotationDir == 2
        viewModel.deviceSettingsList.collectWithLifecycle{
            adapter.submitList(it)
        }

//        viewModel.loadingFlow.collectWithLifecycle {
//            binding.loadingLayout.root.changeVisibility(it)
//        }
    }

    private fun KnobView.setupKnob(knob: KnobDto) {
        if(knob.calibrated.isTrue()) {
            val calibration = knob.calibration.toCalibration()
            calibration.zones1?.let { zone ->
                setHighSinglePosition(zone.highAngle.toFloat())
                if(calibration.rotation != Rotation.DUAL)
                    setMediumPosition(zone.mediumAngle.toFloat())
                setLowSinglePosition(zone.lowAngle.toFloat())
            }
            calibration.zones2?.let { zone ->
                setHighDualPosition(zone.highAngle.toFloat())
                setLowDualPosition(zone.lowAngle.toFloat())
            }
            setOffPosition(calibration.offAngle.toFloat())
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
                    DeviceSettingsItemModel.KnobOrientation -> {
                        navigateSafe(
                            DeviceSettingsFragmentDirections.actionDeviceSettingsFragmentToDirectionSelectionFragment(
                                DirectionSelectionFragmentParams(isEditMode = true, macAddress = viewModel.macAddress)
                            )
                        )
                    }
                    DeviceSettingsItemModel.DeleteKnob -> {
                        showDialog(
                            message = SpannableStringBuilder(getString(R.string.confirm_delete)),
                            positiveButtonText = getString(R.string.delete),
                            isRedPositiveButton = true,
                            onPositiveButtonClick = {
                                binding.loadingLayout.root.visible()
                                viewModel.deleteKnob{
                                    findNavController().popBackStack(R.id.dashboardFragment, false)
                                    binding.loadingLayout.root.gone()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Parcelize
data class DeviceSettingsFragmentParams(val macAddr: String) : Parcelable
