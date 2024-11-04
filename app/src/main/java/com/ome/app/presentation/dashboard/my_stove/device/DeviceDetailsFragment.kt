package com.ome.app.presentation.dashboard.my_stove.device

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.domain.model.network.response.ConnectionState
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.connectionState
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.stove.StoveOrientation
import com.ome.app.presentation.stove.stoveOrientation
import com.ome.app.presentation.views.KnobView
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DeviceDetailsFragment :
    BaseFragment<DeviceDetailsViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceDetailsViewModel by viewModels()

    private val args by navArgs<DeviceDetailsFragmentArgs>()

    private val isEnable = MutableStateFlow(false)

    override fun setupUI() {
        super.setupUI()
        viewModel.initSubscriptions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.macAddress = args.params.macAddr
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        binding.apply {
            name.text = context?.getString(R.string.knob_, args.params.stovePosition)
            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
                knobView.setupKnob(it)
                knobView.setFontSize(18F)
                val batteryLevel = it.battery
                batteryPercentage.text = batteryLevel.addPercentage()
                changeBatteryStates(it.connectStatus.connectionState, batteryLevel)
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
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuDeviceSetting -> {
                    findNavController().navigate(
                        DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceSettingsFragment(
                            DeviceSettingsFragmentParams(
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

            viewModel.knobAngle.value?.let { knobAngle ->
                binding.knobView.setKnobPosition(knobAngle)
            }
        }

//        viewModel.webSocketManager.knobAngleFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {angle ->
//                binding.knobView.setKnobPosition(angle.value.toFloat())
//            }

        viewModel.knobAngle.collectWithLifecycle { angle ->
            binding.knobView.setKnobPosition(angle)
        }

        viewModel.webSocketManager.knobRssiFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
                binding.knobView.changeWiFiState(it.value)
            }
        var connectionState = ConnectionState.Offline
        var batteryLevel = 0
        viewModel.webSocketManager.knobBatteryFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
//                binding.knobView.changeBatteryState(it.value)
                batteryLevel = it.value
                changeBatteryStates(connectionState, batteryLevel)
            }
        viewModel.webSocketManager.knobConnectStatusFlow
            .filter { it?.macAddr == viewModel.macAddress }
            .collectWithLifecycle {
                connectionState = it.value.connectionState
                isEnable.value =connectionState == ConnectionState.Online
                changeBatteryStates(connectionState, batteryLevel)
//                binding.knobView.changeConnectionState(
//                    connectionStatus = it.value,
//                    batteryLevel = mainViewModel.knobs.value.find { knob -> knob.macAddr == viewModel.macAddress }?.battery ?: 0
//                )
            }
        combine(viewModel.webSocketManager.knobConnectStatusFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull(),
            viewModel.webSocketManager.knobRssiFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull(),
            viewModel.webSocketManager.knobBatteryFlow.filter { it?.macAddr == viewModel.macAddress }.filterNotNull()
        ){ knobConnectStatusFlow, knobRssiFlow,  knobBatteryFlow ->
            Triple(knobConnectStatusFlow, knobRssiFlow, knobBatteryFlow)
        }.collectWithLifecycle {
//            binding.knobView.changeConnectionState(
//                connectionStatus = it.first.value,
//                wifiRSSI = it.second.value,
//                batteryLevel = it.third.value
//            )
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
        if(isEnable.value && knob.calibrated.isTrue()) {
            val calibration = knob.calibration.toCalibration()
//            setKnobPosition(knob.angle.toFloat(), calibration.rotationClockWise)
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

    private fun changeBatteryStates(
        connectionState: ConnectionState,
        batteryLevel: Int
    ) {
        binding.status.text = connectionState.name
        when (connectionState) {
            ConnectionState.Charging -> {
                binding.status.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryTransLess)
                binding.status.chipStrokeWidth = 0F
            }

            ConnectionState.Online -> {
                binding.status.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                binding.status.chipStrokeWidth = 0F
                binding.status.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }

            ConnectionState.Offline -> {
                binding.status.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.white)
                binding.status.chipStrokeWidth = 1F
            }
        }
        if (connectionState != ConnectionState.Charging) {
            when (batteryLevel) {
                in 0..9 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_0)
                in 10..20 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_1)
                in 21..38 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_2)
                in 39..50 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_3)
                in 51..62 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_4)
                in 63..75 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_5)
                in 76..94 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_6)
                in 95..100 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_7)
            }
            if (batteryLevel in 0..20)
                binding.batteryIcon.imageTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red)
            else
                binding.batteryIcon.imageTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
        } else {
            binding.batteryIcon.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            when (batteryLevel) {
                in 0..20 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_20)
                in 21..30 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_30)
                in 31..50 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_50)
                in 51..60 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_60)
                in 61..80 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_80)
                in 81..95 -> binding.batteryIcon.setImageResource(R.drawable.ic_charging_90)
                in 96..100 -> binding.batteryIcon.setImageResource(R.drawable.ic_battery_full)
            }
        }
    }

}

@Parcelize
data class DeviceDetailsFragmentParams(val stovePosition: Int, val macAddr: String) : Parcelable