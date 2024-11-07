package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceCalibrationConfirmationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class DeviceCalibrationConfirmationFragment :
    BaseFragment<DeviceCalibrationConfirmationViewModel, FragmentDeviceCalibrationConfirmationBinding>(
        FragmentDeviceCalibrationConfirmationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationConfirmationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationConfirmationFragmentArgs>()

    override fun setupUI() {
        mainViewModel.connectToSocket()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noBtn.setOnClickListener {
            if (viewModel.currentCalibrationStateLiveData.value == null) {
                findNavController().popBackStack()
            } else {
                viewModel.triggerCurrentStepAgain()
            }
        }
        binding.continueBtn.setOnClickListener {
            if (viewModel.currentCalibrationStateLiveData.value == null) {
                showSuccessDialog(
                    title = getString(R.string.warning),
                    message = getString(R.string.ome_knob_will_rotate),
                    onDismiss = {
                        viewModel.nextStep()
                    })
            } else if (viewModel.currentCalibrationStateLiveData.value == CalibrationState.OFF && viewModel.offTriggerCount == 1) {
                showSuccessDialog(
                    title = getString(R.string.warning),
                    message = getString(R.string.ome_knob_manually_turn_to_the_low),
                    onDismiss = {
                        viewModel.currentCalibrationStateLiveData.value = CalibrationState.LOW_SINGLE
                    })
            } else {
                viewModel.nextStep()
            }
        }
        viewModel.firstConfirmationPageLiveData.postValue(true)

        viewModel.isDualKnob = args.params.isDualKnob
        viewModel.macAddress = args.params.macAddr
        viewModel.rotationDir = args.params.rotateDir
        viewModel.offAngle = args.params.offPosition
        viewModel.highSingleAngle = args.params.highSinglePosition
        viewModel.highDualAngle = args.params.highDualPosition
        viewModel.mediumAngle = args.params.medPosition
        viewModel.lowSingleAngle = args.params.lowSinglePosition
        viewModel.lowDualAngle = args.params.lowDualPosition

        binding.topAppBar.setNavigationOnClickListener{
            viewModel.previousStep()
        }
        viewModel.initSubscriptions()

        initLabels()
    }

    override fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.previousStep()
            }
        })

    }

    private fun initLabels() {


    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.previousScreenTriggered) {
            findNavController().popBackStack()
        }
        subscribe(viewModel.calibrationIsDoneLiveData) {
            findNavController().navigate(
                DeviceCalibrationConfirmationFragmentDirections.actionDeviceCalibrationConfirmationFragmentToSetupCompleteFragment(
                    args.params.isComeFromSettings
                )
            )
        }
        subscribe(viewModel.knobAngleLiveData) {
            binding.knobView.setKnobPosition(it)
        }

        subscribe(viewModel.firstConfirmationPageLiveData) {
            binding.labelTv.text =
                getString(R.string.do_the_settings_below_match_your_stove_knob)

        }
        subscribe(viewModel.zoneLiveData) {
            binding.knobView.stovePosition = it
        }
        subscribe(viewModel.currentCalibrationStateLiveData) { currentStep ->
            currentStep?.let {
                binding.noBtn.text = getString(R.string.no_btn)
                if (viewModel.isDualKnob) {
                    when (currentStep) {
                        CalibrationState.HIGH_SINGLE, CalibrationState.LOW_SINGLE -> {
                            binding.labelTv.text =
                                getString(
                                    R.string.calibration_confirmation_dual_label,
                                    currentStep.positionName,
                                    "Single"
                                )
                        }
                        CalibrationState.HIGH_DUAL, CalibrationState.LOW_DUAL -> {
                            binding.labelTv.text =
                                getString(
                                    R.string.calibration_confirmation_dual_label,
                                    currentStep.positionName,
                                    "Dual"
                                )
                        }
                        else -> {
                            binding.labelTv.text =
                                getString(R.string.calibration_confirmation_label, currentStep.positionName)
                        }
                    }
                } else {
                    binding.labelTv.text =
                        getString(R.string.calibration_confirmation_label, currentStep.positionName)
                }

            }
        }

    }
}

@Parcelize
data class DeviceCalibrationConfirmationFragmentParams(
    val isComeFromSettings: Boolean = true,
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
