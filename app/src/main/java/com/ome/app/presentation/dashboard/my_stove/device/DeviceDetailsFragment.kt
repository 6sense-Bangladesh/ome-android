package com.ome.app.presentation.dashboard.my_stove.device

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.util.DeviceProperties.isTablet
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.DialogTimerBinding
import com.ome.app.databinding.FragmentDeviceDetailsBinding
import com.ome.app.domain.TAG
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.state.*
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationConfirmationFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.installation.KnobInstallationManualFragmentParams
import com.ome.app.presentation.views.KnobView
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class DeviceDetailsFragment :
    BaseFragment<DeviceViewModel, FragmentDeviceDetailsBinding>(FragmentDeviceDetailsBinding::inflate) {

    override val viewModel: DeviceViewModel by viewModels()

    private val args by navArgs<DeviceDetailsFragmentArgs>()
    val params by lazy { args.params }

    private val burnerStates
        get() = mainViewModel.getKnobBurnerStatesByMac(params.macAddr)

    private val dialogBinding by lazy { DialogTimerBinding.inflate(layoutInflater) }
    private val dialogTimer by lazy { context?.let {
        AlertDialog.Builder(it)
            .setView(dialogBinding.root)
            .create()
    }}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.macAddress = params.macAddr
        viewModel.initSubscriptions()
    }

    override fun setupUI() {
        viewModel.stovePosition = mainViewModel.getStovePositionByMac(viewModel.macAddress)
        binding.knobView.setFontSize(if(isTablet(resources)) 13f.sp else 6f.sp)
        val selectedColor = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        setupTimer()
        activeTimer()
        binding.apply {
            name.text = context?.getString(R.string.burner_, viewModel.stovePosition)
            knobView.doOnRotationChange(
                doRotate = viewModel.isEnable,
                initAngle = viewModel.initAngle,
                calibration = viewModel.currentKnob.value?.calibration?.toCalibration(viewModel.currentKnob.value?.calibrated),
            ).collectWithLifecycle { rotation ->
                    Log.d(TAG, "doOnRotationChange: $rotation ${viewModel.isEnable.value}")
                    if(viewModel.isEnable.value) {
                        viewModel.changeKnobAngle(rotation)
                        changeBurnerStatus(rotation.toInt())
                    } else stateBaseErrorDialog()
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
        if(BuildConfig.IS_INTERNAL_TESTING){
            binding.tvLevel.setBounceClickListener {
                navigateSafe(
                    DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            macAddress = params.macAddr,
                            isDualKnob = false,
                            isComeFromSettings = true,
                            rotateDir = 1
                        )
                    )
                )
            }
            binding.tvBattery.setBounceClickListener {
                navigateSafe(
                    DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceCalibrationConfirmationFragment(
                        DeviceCalibrationConfirmationFragmentParams(
                            macAddr = params.macAddr,
                            isComeFromSettings = true,
                            offPosition = 0f,
                            isDualKnob = false,
                            lowSinglePosition = 100f,
                            highSinglePosition =160f,
                            lowDualPosition = 200f,
                            highDualPosition = 340f,
                            rotateDir = 1
                        )
                    )
                )
            }
            binding.name.setBounceClickListener {
                navigateSafe(
                    DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToKnobInstallationManualFragment(
                        KnobInstallationManualFragmentParams(macAddr = params.macAddr, isComeFromSettings = true)
                    )
                )
            }
        }
        binding.warningCard.setBounceClickListener {
            navigateSafe(
                DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToKnobInstallationManualFragment(
                    KnobInstallationManualFragmentParams(macAddr = params.macAddr, isComeFromSettings = true)
                )
            )
        }
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuDeviceSetting -> {
                    navigateSafe(DeviceDetailsFragmentDirections.actionDeviceDetailsFragmentToDeviceSettingsFragment(
                        DeviceFragmentParams(macAddr = params.macAddr)
                    ))
                    true
                }
                else -> false
            }
        }
        binding.btnTimer.setBounceClickListener {
            if(viewModel.isEnable.value)
                showTimerDialog()
            else
                stateBaseErrorDialog()
        }
        if(BuildConfig.IS_INTERNAL_TESTING){
            binding.burnerSelection.setBounceClickListener {
                showTimerDialog()
            }
        }
        binding.btnEditTimer.setBounceClickListener {
            val timer = time.toTimer()
            showTimerDialog(timer.first, timer.second, timer.third)
        }
        binding.btnStopTimer.setBounceClickListener {
            binding.timerCard.animateInvisible{
                binding.btnTimer.visible()
            }
            viewModel.stopTimer()
        }
        binding.btnPauseResumeTimer.setBounceClickListener {
            when (binding.btnPauseResumeTimer.tag) {
                R.drawable.ic_pause -> {
                    binding.btnPauseResumeTimer.setIconResource(R.drawable.ic_play)
                    binding.btnPauseResumeTimer.tag = R.drawable.ic_play
                    timerJob?.cancel()
                    viewModel.pauseTimer(time)
                }
                R.drawable.ic_play -> {
                    binding.btnPauseResumeTimer.setIconResource(R.drawable.ic_pause)
                    binding.btnPauseResumeTimer.tag = R.drawable.ic_pause
                    viewModel.resumeTimer()
                }
            }
        }
    }

    private fun stateBaseErrorDialog() {
        if (viewModel.isSafetyLockOn) {
            onError(
                title = getString(R.string.safety_lock_is_on),
                errorMessage = getString(R.string.turn_off_safety_lock)
            )
        } else if(viewModel.currentKnob.value?.calibrated.isFalse()){
            onError(
                title = getString(R.string.knob_not_calibrated),
                errorMessage = getString(R.string.complete_knob_calibration)
            )
        }else {
            onError(
                title = getString(R.string.knob_is_off),
                errorMessage = getString(R.string.manually_turn_the_knob_on)
            )
        }
    }


    private var lastTime = 0L
    private val time
        get() = (lastTime.minus(System.currentTimeMillis()) / 1000).toInt()

    private var timerJob: Job? = null

    private fun activeTimer(){
        timerJob?.cancel()
        timerJob = viewLifecycleScope.launch {
            lastTime = IO { viewModel.pref.getTimer(params.macAddr) }
            Log.d(TAG, "Current time: ${System.currentTimeMillis()}")
            Log.d(TAG,"Last time: $lastTime")
            Log.d(TAG, "activeTimer: $time")
            if(time >= 0) {
                binding.btnPauseResumeTimer.tag = R.drawable.ic_pause
                binding.btnTimer.invisible()
                binding.timerCard.animateVisible()
                while (time > 0) {
                    time.toTimer().let { (hr, min, sec) ->
                        Log.d(TAG, "activeTimer: $hr $min $sec")
                        binding.hour.text = hr.toStringLocale()
                        binding.minute.text = min.toStringLocale()
                        binding.second.text = sec.toStringLocale()
                    }
                    if(time == 0) break
                    else delay(1.seconds)
                    yield()
                }
                binding.timerCard.animateInvisible()
                binding.btnTimer.visible()
            } else if(viewModel.isPauseEnabled.apply { log("isPauseEnabled") }) {
                viewModel.pref.getPauseTime(params.macAddr).let { (hr, min, sec) ->
                    binding.btnPauseResumeTimer.tag = R.drawable.ic_play
                    binding.btnPauseResumeTimer.setIconResource(R.drawable.ic_play)
                    binding.btnTimer.invisible()
                    binding.timerCard.animateVisible()
                    binding.hour.text = hr.toStringLocale()
                    binding.minute.text = min.toStringLocale()
                    binding.second.text = sec.toStringLocale()
                }
            }
        }
    }

    private fun setupTimer(){
        dialogTimer?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBinding.apply {
            pickerHour.maxValue = 11
            pickerMin.maxValue = 59
            pickerSec.maxValue = 59
            listOf(pickerHour, pickerMin, pickerSec).forEach {
                it.setOnValueChangedListener { picker, _, _ ->
                    picker.performSmallHaptic()
                }
            }
            btnCancel.setBounceClickListener {
                dialogTimer?.dismiss()
            }
            btnOkay.setBounceClickListener {
                dialogTimer?.dismiss()
                viewModel.startTurnOffTimer(pickerHour.value, pickerMin.value, pickerSec.value)
            }
        }
    }

    private fun showTimerDialog(hour: Int=0, min: Int=0, sec: Int=0){
        dialogBinding.apply {
            pickerHour.value = hour
            pickerMin.value = min
            pickerSec.value = sec
        }
        dialogTimer?.show()
    }

    override fun setupObserver() {
        super.setupObserver()
        binding.apply {
            viewModel.showTimer.collectWithLifecycle {
                activeTimer()
            }
            viewModel.loadingFlow.collectWithLifecycle {
                binding.loadingLayout.root.changeVisibility(it)
            }
            viewModel.currentKnob.collectWithLifecycle {
                it.log("currentKnob")
                knobView.setupKnob(it)
                viewModel.isSafetyLockOn = it.safetyLock
            }
            mainViewModel.getKnobStateByMac(params.macAddr).collectWithLifecycleStateIn{knob ->
                knob.log("getKnobStateByMac")
                val batteryLevel = knob.battery
                viewModel.isSafetyLockOn = knob.knobSetSafetyMode.orFalse()
                batteryPercentage.text = batteryLevel?.addPercentage().orEmpty()
                knob.angle?.toInt()?.let {
                    changeBurnerStatus(it)
                    if(viewModel.initAngle.value == null)
                        viewModel.initAngle.value = it
//                    if(knob.mountingSurface != null && mainViewModel.userInfo.value.stoveKnobMounting != knob.mountingSurface.type){
//                        viewModel.isEnable.value = false
//                        changeBurnerStatus(it, BurnerState.Off( mainViewModel.getOffAngleByMac(params.macAddr).orZero()))
//                    }
                    binding.knobView.setKnobPosition(it.toFloat())
                }
                knob.connectStatus?.let {
                    changeKnobStatus(it, batteryLevel)
                }
                knob.wifiStrengthPercentage?.let {
//                    knobView.changeWiFiState(it)
                }
                if(knob.knobSetSafetyMode.isTrue()){
                    viewModel.currentKnob.value?.calibration?.offAngle?.let {
                        binding.knobView.setKnobPosition(it.toFloat())
                    }
                }
            }
        }
    }

    private fun KnobView.setupKnob(knob: KnobDto) {
        changeKnobBasicStatus(knob)
        if(knob.calibrated.isFalse() && knob.connectStatus.connectionState != ConnectionState.Offline){
            withDelay(700L){
                tryInMain { binding.warningCard.animateVisible() }
            }
        }
    }

    private fun changeBurnerStatus(currentAngle: Int, vararg states: BurnerState = burnerStates.toTypedArray()) {
        states.minByOrNull {
            KnobAngleManager.normalizeAngle(it.level - currentAngle).let { dif ->
                minOf(dif, 360 - dif)
            }
        }?.apply {
            binding.statusBurner.applyState()  // Let the state apply its specific styles
            viewModel.isEnable.value = type != BurnerState.State.Off
            if(type == BurnerState.State.Off)
                viewModel.initAngle.value = null
        }
    }

    private fun changeKnobStatus(connectionState: ConnectionState, batteryLevel: Int?) {
        binding.statusKnob.text = connectionState.name
        val context = context ?: return
        // Define common style values
        val chipStrokeColor: Int
        val textColor: Int
        val batteryIconTintColor: Int
        val batteryIconResId: Int?
        when (connectionState) {
            ConnectionState.Charging -> {
                chipStrokeColor = R.color.colorPrimaryLight
                textColor = R.color.colorPrimaryDeep
                batteryIconTintColor = R.color.colorPrimary
                batteryIconResId = when (batteryLevel) {
                    in 0..20 -> R.drawable.ic_charging_20
                    in 21..30 -> R.drawable.ic_charging_30
                    in 31..50 -> R.drawable.ic_charging_50
                    in 51..60 -> R.drawable.ic_charging_60
                    in 61..80 -> R.drawable.ic_charging_80
                    in 81..94 -> R.drawable.ic_charging_90
                    in 95..100 -> R.drawable.ic_battery_full
                    else -> null
                }
            }
            ConnectionState.Online, ConnectionState.Offline -> {
                chipStrokeColor = R.color.grayBlue
                textColor = R.color.black
                batteryIconTintColor = if (batteryLevel in 1..20) R.color.red else R.color.black

                batteryIconResId = when (batteryLevel) {
                    in 0..9 -> R.drawable.ic_battery_0
                    in 10..20 -> R.drawable.ic_battery_1
                    in 21..38 -> R.drawable.ic_battery_2
                    in 39..50 -> R.drawable.ic_battery_3
                    in 51..62 -> R.drawable.ic_battery_4
                    in 63..75 -> R.drawable.ic_battery_5
                    in 76..94 -> R.drawable.ic_battery_6
                    in 95..100 -> R.drawable.ic_battery_7
                    else -> null
                }
            }
        }
        if(connectionState == ConnectionState.Offline)
            viewModel.isEnable.value = false

        // Apply common style
        binding.statusKnob.chipStrokeColor = ContextCompat.getColorStateList(context, chipStrokeColor)
        binding.statusKnob.setTextColor(ContextCompat.getColor(context, textColor))

        // Update battery icon and tint
        batteryIconResId?.let {
            binding.batteryIcon.setImageResource(it)
            binding.batteryIcon.visible()
            binding.tvBattery.visible()
            binding.batteryPercentage.visible()
        } ?: run {
            binding.tvBattery.gone()
            binding.batteryIcon.gone()
            binding.batteryPercentage.gone()
        }
        binding.batteryIcon.imageTintList = ContextCompat.getColorStateList(context, batteryIconTintColor)
    }

}

@Keep
@Parcelize
data class DeviceFragmentParams(val macAddr: String) : Parcelable