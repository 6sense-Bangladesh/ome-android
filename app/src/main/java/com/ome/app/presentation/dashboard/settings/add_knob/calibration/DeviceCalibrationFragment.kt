package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentDeviceCalibrationBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class DeviceCalibrationFragment :
    BaseFragment<DeviceCalibrationViewModel, FragmentDeviceCalibrationBinding>(
        FragmentDeviceCalibrationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationFragmentArgs>()

    override fun setupUI() {
        viewModel.macAddress = args.params.macAddr
        viewModel.rotationDir = args.params.rotateDir
        viewModel.isDualKnob = args.params.isDualKnob
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
//                binding.subLabelTv.makeGone()
                binding.labelTv.text =
                    getString(R.string.device_calibration_off_label)
            } else {
//                binding.subLabelTv.makeVisible()
                if (viewModel.isDualKnob) {
                    when (currentStep) {
                        CalibrationState.HIGH_SINGLE, CalibrationState.LOW_SINGLE -> {
                            binding.labelTv.text =
                                getString(
                                    R.string.device_calibration_dual_label,
                                    currentStep.positionName,
                                    "Single"
                                )
                        }
                        CalibrationState.HIGH_DUAL, CalibrationState.LOW_DUAL -> {
                            binding.labelTv.text =
                                getString(
                                    R.string.device_calibration_dual_label,
                                    currentStep.positionName,
                                    "Dual"
                                )
                        }
                        else -> {}
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
    val zoneNumber: Int = 0,
    val isDualKnob: Boolean = false,
    val rotateDir: Int = 0,
    val macAddr: String = ""
) : Parcelable


