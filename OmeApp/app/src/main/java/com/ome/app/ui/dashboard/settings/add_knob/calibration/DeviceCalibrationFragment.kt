package com.ome.app.ui.dashboard.settings.add_knob.calibration

import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentDeviceCalibrationBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize


@AndroidEntryPoint
class DeviceCalibrationFragment :
    BaseFragment<DeviceCalibrationViewModel, FragmentDeviceCalibrationBinding>(
        FragmentDeviceCalibrationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }

        viewModel.clearData()
        binding.skipTv.setOnClickListener {
            showDialog(
                title = getString(R.string.attention),
                message = SpannableStringBuilder(getString(R.string.attention_skip_device_calibration_label)),
                onPositiveButtonClick = {
                    findNavController().navigate(
                        DeviceCalibrationFragmentDirections.actionDeviceCalibrationFragmentToSetupCompleteFragment(
                            args.params.isComeFromSettings
                        )
                    )
                }
            )
        }
        binding.continueBtn.setOnClickListener {
            viewModel.setLabel()
        }
        binding.skipTv.makeGone()
        viewModel.currentCalibrationStateLiveData.postValue(CalibrationState.OFF)
        viewModel.macAddress = args.params.macAddr
        viewModel.rotationDir = args.params.rotateDir
        viewModel.isDualKnob = args.params.isDualKnob

        binding.backIv.setOnClickListener {
            viewModel.previousStep()
        }
        viewModel.initSubscriptions()

    }


    override fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.previousStep()
            }
        })

    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.previousScreenTriggered) {
            findNavController().popBackStack()
        }
        subscribe(viewModel.calibrationIsDoneLiveData) {
            if (it) {
                findNavController().navigate(
                    DeviceCalibrationFragmentDirections.actionDeviceCalibrationFragmentToDeviceCalibrationConfirmationFragment(
                        DeviceCalibrationConfirmationFragmentParams(
                            isComeFromSettings = args.params.isComeFromSettings,
                            offPosition = viewModel.offAngle ?: 0f,
                            lowPosition = viewModel.lowAngle ?: 0f,
                            medPosition = viewModel.mediumAngle ?: 0f,
                            highPosition = viewModel.highAngle ?: 0f,
                            macAddr = viewModel.macAddress,
                            isDualKnob = args.params.isDualKnob,
                            rotateDir = viewModel.rotationDir ?: -1
                        )
                    )
                )
            }


//            binding.labelTv.text =
//                getString(R.string.calibration_confirmation_label)
//            binding.continueBtn.text = getString(R.string.yes_btn)
        }
        subscribe(viewModel.knobAngleLiveData) {
            binding.knobView.setKnobPosition(it)
        }

        subscribe(viewModel.labelLiveData) {
            when (it.first) {
                CalibrationState.OFF -> binding.knobView.setOffPosition(it.second)
                CalibrationState.LOW -> binding.knobView.setLowPosition(it.second)
                CalibrationState.MEDIUM -> binding.knobView.setMediumPosition(it.second)
                CalibrationState.HIGH -> binding.knobView.setHighPosition(it.second)
            }
            viewModel.nextStep()
        }

        subscribe(viewModel.zoneLiveData) {
            binding.knobView.setStovePosition(it)
        }
        subscribe(viewModel.currentCalibrationStateLiveData) { currentStep ->
            binding.knobView.hideLabel(currentStep)
            if (currentStep == CalibrationState.OFF) {
                binding.subLabelTv.makeGone()
                binding.skipTv.makeGone()
            } else {
                binding.subLabelTv.makeVisible()
                binding.skipTv.makeVisible()
            }
            binding.labelTv.text =
                getString(R.string.device_calibration_label, currentStep.name)
        }

    }
}

@Parcelize
data class DeviceCalibrationFragmentParams(
    val isComeFromSettings: Boolean = true,
    val zoneNumber: Int = 0,
    val isDualKnob: Boolean = false,
    val rotateDir: Int = 0,
    val macAddr: String = ""
) : Parcelable


