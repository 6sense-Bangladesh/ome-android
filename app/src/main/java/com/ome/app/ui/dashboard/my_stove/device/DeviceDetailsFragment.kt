package com.ome.app.ui.dashboard.my_stove.device

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.domain.model.network.response.ConnectionState
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.connectionState
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.stoveOrientation
import com.ome.app.ui.views.KnobView
import com.ome.app.utils.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull


class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by activityViewModels()

    private val args by navArgs<DeviceSettingsFragmentArgs>()

    private val isEnable = MutableStateFlow(false)

    @OptIn(FlowPreview::class)
    override fun setupUI() {
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        binding.apply {
            name.text = context?.getString(R.string.knob_, args.params.stovePosition)
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knobView.setupKnob(it)
                knobView.setFontSize(18F)
                val batteryLevel = it.battery
                batteryPercentage.text = batteryLevel.addPercentage()
                it.connectStatus.connectionState.apply {
                    status.text = name
                    when(this){
                        ConnectionState.Charging -> {
                            status.chipBackgroundColor  = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryTransLess)
                            status.chipStrokeWidth = 0F
                        }
                        ConnectionState.Online -> {
                            status.chipBackgroundColor  = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                            status.chipStrokeWidth = 0F
                            status.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                        ConnectionState.Offline -> {
                            status.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
                            status.chipStrokeWidth = 1F
                        }
                    }
                    if(this != ConnectionState.Charging){
                        when (batteryLevel) {
                            in 0 .. 9 -> batteryIcon.setImageResource(R.drawable.ic_battery_0)
                            in 10..20 -> batteryIcon.setImageResource(R.drawable.ic_battery_1)
                            in 21..38 -> batteryIcon.setImageResource(R.drawable.ic_battery_2)
                            in 39..50 -> batteryIcon.setImageResource(R.drawable.ic_battery_3)
                            in 51..62 -> batteryIcon.setImageResource(R.drawable.ic_battery_4)
                            in 63..75 -> batteryIcon.setImageResource(R.drawable.ic_battery_5)
                            in 76..94 -> batteryIcon.setImageResource(R.drawable.ic_battery_6)
                            in 95..100 -> batteryIcon.setImageResource(R.drawable.ic_battery_7)
                        }
                        if(batteryLevel in 0..20)
                            batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                        else
                            batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
                    }else{
                        batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                        when (batteryLevel) {
                            in 0 .. 20 -> batteryIcon.setImageResource(R.drawable.ic_charging_20)
                            in 21..30 -> batteryIcon.setImageResource(R.drawable.ic_charging_30)
                            in 31..50 -> batteryIcon.setImageResource(R.drawable.ic_charging_50)
                            in 51..60 -> batteryIcon.setImageResource(R.drawable.ic_charging_60)
                            in 61..80 -> batteryIcon.setImageResource(R.drawable.ic_charging_80)
                            in 81..95 -> batteryIcon.setImageResource(R.drawable.ic_charging_90)
                            in 96..100 -> batteryIcon.setImageResource(R.drawable.ic_battery_full)
                        }
                    }
                }
            }
            knobView.doOnRotationChange(doRotate = isEnable)
//                .debounce(700)
                .collectWithLifecycle { rotation ->
                    Log.d(TAG, "doOnRotationChange: $rotation")
                    if(isEnable.value)
                        viewModel.changeKnobAngle(rotation)
                    else{
                        onError(
                            title = "Knob is Off",
                            errorMessage = getString(R.string.manually_turn_the_knob_on)
                        )
                    }
                }
            burnerSelection.visible()
            mainViewModel.userInfo.value.stoveOrientation.stoveOrientation.let {
                when(it){
                    StoveOrientation.FOUR_BURNERS -> {
                        visible(knob1, knob2, knob4, knob5, spacer)
                        gone(knob3, knob6, knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob4.backgroundTintList = selectedColor
                            4 -> knob5.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.FOUR_BAR_BURNERS, StoveOrientation.FIVE_BURNERS ->{
                        visible(knob1, knob2, knob4, knob5, knob7)
                        gone(knob3, knob6, spacer)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob4.backgroundTintList = selectedColor
                            4 -> knob5.backgroundTintList = selectedColor
                            5 -> knob7.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.SIX_BURNERS -> {
                        visible(knob1, knob2, knob3, knob4, knob5, knob6, spacer)
                        gone(knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob3.backgroundTintList = selectedColor
                            4 -> knob4.backgroundTintList = selectedColor
                            5 -> knob5.backgroundTintList = selectedColor
                            6 -> knob6.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.TWO_BURNERS_HORIZONTAL ->{
                        visible(knob1, knob2)
                        gone(knob3, knob4, knob5, knob6, knob7, spacer)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
                        visible(knob1, knob4, spacer)
                        gone(knob2, knob3, knob5, knob6, knob7)
                        when(args.params.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob4.backgroundTintList = selectedColor
                        }
                    }
                    null -> burnerSelection.gone()
                }
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuDeviceSetting -> {
                    findNavController().navigate(
                        DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceSettingsFragment(
                            DeviceFragmentParams(
                                stovePosition = args.params.stovePosition,
                                macAddr = args.params.macAddr
                            )
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.zonesLiveData) {
            if (it.rotationDir == 2) {
                binding.knobView.setOffPosition(it.offAngle.toFloat())
                if (it.zones[0].zoneName == "Single") {
                    binding.knobView.setLowSinglePosition(it.zones[0].lowAngle.toFloat())
                    binding.knobView.setHighSinglePosition(it.zones[0].highAngle.toFloat())
                } else {
                    binding.knobView.setLowDualPosition(it.zones[0].lowAngle.toFloat())
                    binding.knobView.setHighDualPosition(it.zones[0].highAngle.toFloat())
                }
                if (it.zones[1].zoneName == "Dual") {
                    binding.knobView.setLowDualPosition(it.zones[1].lowAngle.toFloat())
                    binding.knobView.setHighDualPosition(it.zones[1].highAngle.toFloat())
                } else {
                    binding.knobView.setLowSinglePosition(it.zones[1].lowAngle.toFloat())
                    binding.knobView.setHighSinglePosition(it.zones[1].highAngle.toFloat())
                }
            } else {
                binding.knobView.setOffPosition(it.offAngle.toFloat())
                binding.knobView.setLowSinglePosition(it.zones[0].lowAngle.toFloat())
                binding.knobView.setMediumPosition(it.zones[0].mediumAngle.toFloat())
                binding.knobView.setHighSinglePosition(it.zones[0].highAngle.toFloat())
            }

        }

        subscribe(viewModel.knobAngleLiveData) { angle ->
            angle?.let { binding.knobView.setKnobPosition(it) }
        }

        viewModel.webSocketManager.knobRssiFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
                binding.knobView.changeWiFiState(it.value)
            }
        viewModel.webSocketManager.knobBatteryFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
                binding.knobView.changeBatteryState(it.value)
            }
        viewModel.webSocketManager.knobConnectStatusFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
                isEnable.value = it.value.connectionState == ConnectionState.Online
            }
        combine(viewModel.webSocketManager.knobConnectStatusFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull(),
            viewModel.webSocketManager.knobRssiFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull(),
            viewModel.webSocketManager.knobBatteryFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull()
        ){ knobConnectStatusFlow, knobRssiFlow,  knobBatteryFlow ->
            Triple(knobConnectStatusFlow, knobRssiFlow, knobBatteryFlow)
        }.collectWithLifecycle {
            binding.knobView.changeConnectionState(
                connectionStatus = it.first.value,
                wifiRSSI = it.second.value,
                batteryLevel = it.third.value
            )
        }
//        viewModel.webSocketManager.knobConnectStatusFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {
//                binding.knobView.changeConnectionState(
//                    connectionStatus = it.value.connectionState,
//                    wifiRSSI = it.value.rssi,
//                    batteryLevel = it.value.battery
//                )
//            }
    }

    private fun KnobView.setupKnob(knob: KnobDto) {
        isEnable.value = changeKnobBasicStatus(knob)
        if(isEnable.value) {
            val calibration = knob.calibration.toCalibration()
            setStovePosition(knob.stovePosition)
            setKnobPosition(knob.angle.toFloat(), calibration.rotationClockWise)
            calibration.zones1?.let { zone ->
                setHighSinglePosition(zone.highAngle.toFloat())
                setMediumPosition(zone.mediumAngle.toFloat())
                setLowSinglePosition(zone.lowAngle.toFloat())
            }
            calibration.zones2?.let { zone ->
                setHighDualPosition(zone.highAngle.toFloat())
//            setMediumDualPosition(zone.mediumAngle.toFloat())
                setLowDualPosition(zone.lowAngle.toFloat())
            }
            setOffPosition(calibration.offAngle.toFloat())
        }
    }

}