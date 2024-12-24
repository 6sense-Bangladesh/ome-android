package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceCalibrationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class DeviceCalibrationFragment :
    BaseFragment<DeviceCalibrationViewModel, FragmentDeviceCalibrationBinding>(
        FragmentDeviceCalibrationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationFragmentArgs>()
    val params by lazy { args.params }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.macAddress = params.macAddress
        viewModel.initSubscriptions()
    }

    override fun setupUI() {
        viewModel.rotationDir = params.rotateDir
        viewModel.isDualKnob = params.isDualKnob
        if(viewModel.isDualKnob)
            binding.labelZone.text = getString(R.string.dual_zone_knob)
        else
            binding.labelZone.text = getString(R.string.single_zone_knob)
        binding.knobView.enableFullLabel()
        binding.labelZone.text = if(params.isDualKnob)
            getString(R.string.dual_zone_knob)
        else
            getString(R.string.single_zone_knob)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearData()
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.noBtn.setBounceClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            viewModel.setLabel()
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
        binding.topAppBar.setOnMenuItemClickListener{ menu->
           when(menu.itemId){
                R.id.action_turn_off_safety_lock -> {
                    mainViewModel.setSafetyLockOff(params.macAddress).also {
                        Snackbar.make(requireContext(), binding.root, "Safety lock turned off", Snackbar.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.action_skip_calibration -> {
                    popBackSafe(
                        if (params.isComeFromSettings)
                            R.id.deviceDetailsFragment
                        else
                            R.id.dashboardFragment
                    )
                    true
                }

               else -> false
           }
        }
    }


    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){
            viewModel.previousStep()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.previousScreenTriggered) {
            popBackSafe()
        }
        viewModel.calibrationIsDoneFlow.collectWithLifecycle {
            navigateSafe(
                DeviceCalibrationFragmentDirections.actionDeviceCalibrationFragmentToDeviceCalibrationConfirmationFragment(
                    DeviceCalibrationConfirmationFragmentParams(
                        isComeFromSettings = params.isComeFromSettings,
                        offPosition = viewModel.offAngle.orZero(),
                        lowSinglePosition = viewModel.lowSingleAngle.orZero(),
                        lowDualPosition = viewModel.lowDualAngle.orZero(),
                        medPosition = viewModel.mediumAngle.orZero(),
                        highSinglePosition = viewModel.highSingleAngle.orZero(),
                        highDualPosition = viewModel.highDualAngle.orZero(),
                        macAddr = viewModel.macAddress,
                        isDualKnob = params.isDualKnob,
                        rotateDir = viewModel.rotationDir.orMinusOne()
                    )
                )
            )
            lifecycleScope.launch {
                viewModel.currentCalibrationState.emit(CalibrationState.OFF)
            }
        }
        viewModel.knobAngleFlow.collectWithLifecycle{
            binding.knobView.setKnobPosition(it)
        }

        subscribe(viewModel.labelLiveData) {
            when (it.first) {
                CalibrationState.OFF, CalibrationState.MOVE_OFF -> binding.knobView.setOffPosition(it.second)
                CalibrationState.LOW_SINGLE -> binding.knobView.setLowSinglePosition(it.second)
                CalibrationState.MEDIUM -> binding.knobView.setMediumPosition(it.second)
                CalibrationState.HIGH_SINGLE -> binding.knobView.setHighSinglePosition(it.second)
                CalibrationState.HIGH_DUAL -> binding.knobView.setHighDualPosition(it.second)
                CalibrationState.LOW_DUAL -> binding.knobView.setLowDualPosition(it.second)
            }
        }
        subscribe(viewModel.zoneLiveData) {
            binding.knobView.stovePosition = it
        }
        viewModel.currentCalibrationState.collectWithLifecycle{ currentStep ->
            currentStep.log("nextStep current")
            val currentIndex = if (!params.isDualKnob)
                viewModel.calibrationStatesSequenceSingleZone.indexOf(currentStep)
            else
                viewModel.calibrationStatesSequenceDualZone.indexOf(currentStep)
            binding.noBtn.changeVisibility(currentIndex != 0)

            binding.knobView.hideLabel(currentStep)
            if (currentStep == CalibrationState.OFF) {
                binding.labelTv.text =
                    getString(R.string.device_calibration_off_label).asHtml
            } else {
                if (viewModel.isDualKnob) {
                    when (currentStep) {
                        CalibrationState.HIGH_SINGLE, CalibrationState.LOW_SINGLE -> {
                            binding.labelTv.text =
                                getString(R.string.device_calibration_dual_label, currentStep.positionName, "First").asHtml
                        }
                        CalibrationState.HIGH_DUAL, CalibrationState.LOW_DUAL -> {
                            binding.labelTv.text =
                                getString(R.string.device_calibration_dual_label, currentStep.positionName, "Second").asHtml
                        }
                        else -> Unit
                    }
                } else {
                    binding.labelTv.text =
                        getString(R.string.device_calibration_label, currentStep.positionName).asHtml
                }

            }


        }

    }
}

@Keep
@Parcelize
data class DeviceCalibrationFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isDualKnob: Boolean = false,
    val rotateDir: Int? = null,
    val macAddress: String = ""
) : Parcelable


