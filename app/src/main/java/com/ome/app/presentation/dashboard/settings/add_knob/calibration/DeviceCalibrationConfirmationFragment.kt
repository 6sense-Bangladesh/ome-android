package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.content.Context
import android.os.Parcelable
import androidx.activity.addCallback
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceCalibrationConfirmationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class DeviceCalibrationConfirmationFragment :
    BaseFragment<DeviceCalibrationConfirmationViewModel, FragmentDeviceCalibrationConfirmationBinding>(
        FragmentDeviceCalibrationConfirmationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationConfirmationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationConfirmationFragmentArgs>()
    val params by lazy { args.params }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.macAddress = params.macAddr
        viewModel.initSubscriptions()
    }

    override fun setupUI() {
        viewModel.isDualKnob = params.isDualKnob
        viewModel.rotationDir = params.rotateDir
        viewModel.offAngle = params.offPosition
        viewModel.highSingleAngle = params.highSinglePosition
        viewModel.highDualAngle = params.highDualPosition
        viewModel.mediumAngle = params.medPosition
        viewModel.lowSingleAngle = params.lowSinglePosition
        viewModel.lowDualAngle = params.lowDualPosition
        binding.knobView.enableFullLabel()
        initLabels()
        binding.labelZone.text = if(params.isDualKnob)
            getString(R.string.dual_zone_knob)
        else
            getString(R.string.single_zone_knob)
        if (viewModel.currentCalibrationState.value == null) {
            showSuccessDialog(
                title = getString(R.string.warning),
                message = getString(R.string.ome_knob_will_rotate),
                onDismiss = {
                    startCalibration()
                })
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.noBtn.setBounceClickListener{
            popBackSafe()
        }
        binding.continueBtn.setBounceClickListener {
            if (viewModel.currentCalibrationState.value == null) {
                showSuccessDialog(
                    title = getString(R.string.warning),
                    message = getString(R.string.ome_knob_will_rotate),
                    onDismiss = {
                        startCalibration()
                    })
            }
            else startCalibration()
        }
        binding.topAppBar.setOnMenuItemClickListener{ menu->
            when(menu.itemId){
                R.id.action_turn_off_safety_lock -> {
                    mainViewModel.setSafetyLockOff(listOf(params.macAddr))
                    toast("Safety lock turned off")
                    true
                }
                R.id.action_skip_calibration -> {
                    if (params.isComeFromSettings)
                        popBackSafe(R.id.deviceSettingsFragment) ?: popBackSafe(R.id.deviceDetailsFragment)
                    else
                        popBackSafe(R.id.dashboardFragment, false) ?: navigateSafe(R.id.actionDashboardFragment)
                    true
                }
                else -> false
            }
        }
        if(BuildConfig.IS_INTERNAL_TESTING){
            binding.labelZone.setBounceClickListener {
                binding.continueBtn.startAnimation()
                lifecycleScope.launch(Dispatchers.IO){
                    viewModel.setCalibration()
                }
            }
        }
    }

    private fun startCalibration() {
        if(!viewLifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        viewModel.nextStep()
        binding.continueBtn.startAnimation()
    }

    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){
            viewModel.previousStep()
            binding.continueBtn.revertAnimation()
        }
    }

    private fun initLabels() {
        viewModel.offAngle?.let { binding.knobView.setOffPosition(it) }
        viewModel.highSingleAngle?.let { binding.knobView.setHighSinglePosition(it) }
        viewModel.lowSingleAngle?.let { binding.knobView.setLowSinglePosition(it) }
        if(!params.isDualKnob) {
            viewModel.mediumAngle?.let { binding.knobView.setMediumPosition(it) }
        }
        else {
            viewModel.highDualAngle?.let { binding.knobView.setHighDualPosition(it) }
            viewModel.lowDualAngle?.let { binding.knobView.setLowDualPosition(it) }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.previousScreenTriggered) {
            popBackSafe()
        }
        viewModel.calibrationIsDoneFlow.collectWithLifecycle {
            navigateSafe(
                DeviceCalibrationConfirmationFragmentDirections.actionDeviceCalibrationConfirmationFragmentToSetupCompleteFragment(
                    params.isComeFromSettings
                )
            )
        }
        viewModel.knobAngleFlow.collectWithLifecycle{
            binding.continueBtn.revertAnimation()
            binding.knobView.setKnobPosition(it)
        }

//        subscribe(viewModel.firstConfirmationPageLiveData) {
//            binding.labelTv.text = getString(R.string.calibration_confirmation_label, viewModel.currentCalibrationState.value?.name)
//        }
        subscribe(viewModel.zoneLiveData) {
            binding.knobView.stovePosition = it
        }
        viewModel.currentCalibrationState.collectWithLifecycle{ currentStep ->
//            when(currentStep){
//                CalibrationState.OFF -> viewModel.offAngle?.let { binding.knobView.setOffPosition(it) }
//                CalibrationState.HIGH_SINGLE -> viewModel.highSingleAngle?.let { binding.knobView.setHighSinglePosition(it) }
//                CalibrationState.HIGH_DUAL -> viewModel.highDualAngle?.let { binding.knobView.setHighDualPosition(it) }
//                CalibrationState.MEDIUM -> viewModel.mediumAngle?.let { binding.knobView.setMediumPosition(it) }
//                CalibrationState.LOW_SINGLE -> viewModel.lowSingleAngle?.let { binding.knobView.setLowSinglePosition(it) }
//                CalibrationState.LOW_DUAL -> viewModel.lowDualAngle?.let { binding.knobView.setLowDualPosition(it) }
//            }

            currentStep.log("nextStep current")
            if (viewModel.isDualKnob) {
                when (currentStep) {
                    CalibrationState.HIGH_SINGLE -> {
                        binding.labelTv.text =
                            getString(R.string.calibration_confirmation_dual_label, currentStep.positionName, "First").asHtml
                    }
                    CalibrationState.LOW_SINGLE -> {
                        binding.labelTv.text =
                            getString(R.string.device_calibration_dual_label, currentStep.positionName, "First").asHtml
                    }
                    CalibrationState.HIGH_DUAL, CalibrationState.LOW_DUAL -> {
                        binding.labelTv.text =
                            getString(R.string.calibration_confirmation_dual_label, currentStep.positionName, "Second").asHtml
                    }
                    else -> {
                        binding.labelTv.text =
                            getString(R.string.calibration_confirmation_label, currentStep.positionName).asHtml
                    }
                }
            }else
                binding.labelTv.text = getString(R.string.calibration_confirmation_label, currentStep.positionName).asHtml
        }

        if(BuildConfig.IS_INTERNAL_TESTING){
            binding.knobView.doOnRotationChange(
                doRotate = MutableStateFlow(true),
                initAngle = viewModel.initAngle,
                calibration = null
            ).collectWithLifecycle {
                it.log("doOnRotationChange")
                viewModel.knobAngleFlow.value = it
            }
        }

    }
}

@Keep
@Parcelize
data class DeviceCalibrationConfirmationFragmentParams(
    val isComeFromSettings: Boolean = false,
    val offPosition: Float = 0f,
    val lowSinglePosition: Float = 0f,
    val lowDualPosition: Float = 0f,
    val medPosition: Float = 0f,
    val highSinglePosition: Float = 0f,
    val highDualPosition: Float = 0f,
    val isDualKnob: Boolean = false,
    val rotateDir: Int = 0,
    val macAddr: String = ""
) : Parcelable
