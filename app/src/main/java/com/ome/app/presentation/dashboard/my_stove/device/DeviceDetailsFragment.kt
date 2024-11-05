package com.ome.app.presentation.dashboard.my_stove.device

import android.os.Parcelable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.domain.model.network.response.Calibration.Rotation
import com.ome.app.domain.model.network.response.ConnectionState
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.stove.StoveOrientation
import com.ome.app.presentation.stove.stoveOrientation
import com.ome.app.presentation.stove.stoveType
import com.ome.app.presentation.views.KnobView
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by viewModels()

    private val args by navArgs<DeviceDetailsFragmentArgs>()


    override fun setupUI() {
        viewModel.macAddress = args.params.macAddr
        viewModel.stovePosition = mainViewModel.getStovePositionByMac(viewModel.macAddress)
        viewModel.initSubscriptions()
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        binding.apply {
            name.text = context?.getString(R.string.burner_, viewModel.stovePosition)
//            mainViewModel.knobs.value.find { it.macAddr == args.params.macAddr }?.let {
//                knobView.setFontSize(18F)
//                val batteryLevel = it.battery
//                batteryPercentage.text = batteryLevel.addPercentage()
//                changeBatteryStates(it.connectStatus.connectionState, batteryLevel)
//            }
            knobView.doOnRotationChange(doRotate = viewModel.isEnable)
//                .debounce(700)
                .collectWithLifecycle { rotation ->
                    Log.d(TAG, "doOnRotationChange: $rotation ${viewModel.isEnable.value}")
                    if(viewModel.isEnable.value)
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
                        when(viewModel.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                            3 -> knob4.backgroundTintList = selectedColor
                            4 -> knob5.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.FOUR_BAR_BURNERS, StoveOrientation.FIVE_BURNERS ->{
                        visible(knob1, knob2, knob4, knob5, knob7)
                        gone(knob3, knob6, spacer)
                        when(viewModel.stovePosition){
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
                        when(viewModel.stovePosition){
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
                        when(viewModel.stovePosition){
                            1 -> knob1.backgroundTintList = selectedColor
                            2 -> knob2.backgroundTintList = selectedColor
                        }
                    }
                    StoveOrientation.TWO_BURNERS_VERTICAL -> {
                        visible(knob1, knob4, spacer)
                        gone(knob2, knob3, knob5, knob6, knob7)
                        when(viewModel.stovePosition){
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
                            DeviceSettingsFragmentParams(macAddr = args.params.macAddr)
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
        binding.apply {
            viewModel.currentKnob.collectWithLifecycle {
                it.log("currentKnob")
                knobView.setupKnob(it)
            }
            mainViewModel.getKnobStateByMac(args.params.macAddr).collectWithLifecycle{knob ->
                val batteryLevel = knob.battery
                var isCharging : ConnectionState? = null
                batteryPercentage.text = batteryLevel?.addPercentage().orEmpty()
                knob.connectStatus?.let {
                    changeBatteryStates(it, batteryLevel)
                    if(it == ConnectionState.Charging)
                        isCharging = it
                }
//                knob.mountingSurface?.let {
//                    if(mainViewModel.userInfo.value.stoveType?.mounting != knob.mountingSurface.key){
//                        changeBatteryStates(isCharging ?: ConnectionState.Offline, batteryLevel)
//                        viewModel.isEnable.value = false
//                    }
//                    else{
//                        changeBatteryStates(isCharging ?: ConnectionState.Online, batteryLevel)
//                        viewModel.isEnable.value = true
//                    }
//                }
                knob.angle?.toFloat()?.let {
                    if(it == mainViewModel.getOffAngleByMac(args.params.macAddr) ||
                        (knob.mountingSurface != null && mainViewModel.userInfo.value.stoveType?.mounting != knob.mountingSurface.key)
                    ){
                        changeBatteryStates(isCharging ?: ConnectionState.Offline, batteryLevel)
                        viewModel.isEnable.value = false
                    }else {
                        viewModel.isEnable.value = true
                        changeBatteryStates(isCharging ?: ConnectionState.Online, batteryLevel)
                    }
                    binding.knobView.setKnobPosition(it)
                    // TODO : fix needed - separate status knob & burner
                }
                knob.log("getKnobStateByMac ${viewModel.isEnable.value}")
                knob.wifiStrengthPercentage?.let {
//                    knobView.changeWiFiState(it)
                }
            }
        }

//        viewModel.webSocketManager.knobAngleFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {angle ->
//                binding.knobView.setKnobPosition(angle.value.toFloat())
//            }

//        viewModel.knobAngle.collectWithLifecycle { angle ->
//            if(angle == viewModel.offAngle){
//                changeBatteryStates(ConnectionState.Offline)
//                viewModel.isEnable.value = false
//            }else
//                viewModel.isEnable.value = true
//            binding.knobView.setKnobPosition(angle)
//        }

//        viewModel.webSocketManager.knobMountingSurfaceFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {
//                when(it.value){
//                    "vertical" -> {
//                        changeBatteryStates(ConnectionState.Online)
//                        viewModel.isEnable.value = true
//                    }
//                    "horizontal" -> {
//                        changeBatteryStates(ConnectionState.Offline)
//                        viewModel.isEnable.value = false
//                    }
//                }
//            }

//        viewModel.webSocketManager.knobRssiFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {
//                binding.knobView.changeWiFiState(it.value)
//            }
//        var connectionState = ConnectionState.Offline
//        var batteryLevel = 0
//        viewModel.webSocketManager.knobBatteryFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {
////                binding.knobView.changeBatteryState(it.value)
//                batteryLevel = it.value
//                changeBatteryStates(connectionState, batteryLevel)
//            }
//        viewModel.webSocketManager.knobConnectStatusFlow
//            .filter { it?.macAddr == viewModel.macAddress }
//            .collectWithLifecycle {
//                connectionState = it.value.connectionState
//                viewModel.isEnable.value =connectionState == ConnectionState.Online
//                changeBatteryStates(connectionState, batteryLevel)
////                binding.knobView.changeConnectionState(
////                    connectionStatus = it.value,
////                    batteryLevel = mainViewModel.knobs.value.find { knob -> knob.macAddr == viewModel.macAddress }?.battery ?: 0
////                )
//            }
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
        changeKnobBasicStatus(knob)
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

    private fun changeBatteryStates(
        connectionState: ConnectionState,
        batteryLevel: Int? = null
    ) {
        binding.status.text = connectionState.name
        when (connectionState) {
            ConnectionState.Charging -> {
                binding.status.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryTransLess)
                binding.status.chipStrokeWidth = 0F
                binding.status.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            ConnectionState.Online -> {
                binding.status.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                binding.status.chipStrokeWidth = 0F
                binding.status.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            ConnectionState.Offline -> {
                binding.status.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
                binding.status.chipStrokeWidth = 1F
                binding.status.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
        if (connectionState == ConnectionState.Charging) {
            binding.batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
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
        else{
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
                binding.batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            else
                binding.batteryIcon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
        }
    }

}

@Parcelize
data class DeviceDetailsFragmentParams(val macAddr: String) : Parcelable