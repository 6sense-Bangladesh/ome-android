package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceCalibrationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class DeviceCalibrationFragment :
    BaseFragment<DeviceCalibrationViewModel, FragmentDeviceCalibrationBinding>(
        FragmentDeviceCalibrationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationFragmentArgs>()

    override fun setupUI() {
        viewModel.macAddress = args.params.macAddress
        viewModel.rotationDir = args.params.rotateDir
        viewModel.isDualKnob = args.params.isDualKnob
        if(viewModel.isDualKnob)
            binding.labelZone.text = getString(R.string.dual_zone_knob)
        else
            binding.labelZone.text = getString(R.string.single_zone_knob)
        mainViewModel.connectToSocket()
        binding.knobView.enableFullLabel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.clearData()

        viewModel.initSubscriptions()

    }

    override fun setupListener() {
        binding.continueBtn.setBounceClickListener {
            viewModel.setLabel()
        }
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
    }


    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){
            viewModel.previousStep()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.previousScreenTriggered) {
            findNavController().popBackStack()
        }
        subscribe(viewModel.calibrationIsDoneLiveData) {
            if (it) {
                if(viewModel.isDualKnob){
                    lifecycleScope.launch {
                        binding.continueBtn.startAnimation()
                        viewModel.setCalibration()
                        navigateSafe(DeviceCalibrationFragmentDirections.actionDeviceCalibrationFragmentToSetupCompleteFragment(false))
                    }
                    return@subscribe
                }
                navigateSafe(
                    DeviceCalibrationFragmentDirections.actionDeviceCalibrationFragmentToDeviceCalibrationConfirmationFragment(
                        DeviceCalibrationConfirmationFragmentParams(
                            isComeFromSettings = args.params.isComeFromSettings,
                            offPosition = viewModel.offAngle ?: 0f,
                            lowSinglePosition = viewModel.lowSingleAngle ?: 0f,
                            lowDualPosition = viewModel.lowDualAngle ?: 0f,
                            medPosition = viewModel.mediumAngle ?: 0f,
                            highSinglePosition = viewModel.highSingleAngle ?: 0f,
                            highDualPosition = viewModel.highDualAngle ?: 0f,
                            macAddr = viewModel.macAddress,
                            isDualKnob = args.params.isDualKnob,
                            rotateDir = viewModel.rotationDir ?: -1
                        )
                    )
                )
            }
        }
        viewModel.knobAngleFlow.collectWithLifecycle{
            binding.knobView.setKnobPosition(it)
        }

        subscribe(viewModel.labelLiveData) {
            when (it.first) {
                CalibrationState.OFF -> binding.knobView.setOffPosition(it.second)
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
                        getString(R.string.device_calibration_label, currentStep.positionName)
                }

            }


        }

    }
}

@Parcelize
data class DeviceCalibrationFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isDualKnob: Boolean = false,
    val rotateDir: Int? = null,
    val macAddress: String = ""
) : Parcelable


