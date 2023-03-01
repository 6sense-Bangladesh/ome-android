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
import com.ome.Ome.databinding.FragmentDeviceCalibrationConfirmationBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize


@AndroidEntryPoint
class DeviceCalibrationConfirmationFragment :
    BaseFragment<DeviceCalibrationConfirmationViewModel, FragmentDeviceCalibrationConfirmationBinding>(
        FragmentDeviceCalibrationConfirmationBinding::inflate
    ) {
    override val viewModel: DeviceCalibrationConfirmationViewModel by viewModels()

    private val args by navArgs<DeviceCalibrationConfirmationFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.noBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }

        binding.skipTv.setOnClickListener {
            showDialog(
                title = getString(R.string.attention),
                message = SpannableStringBuilder(getString(R.string.attention_skip_device_calibration_label)),
                onPositiveButtonClick = {
                    findNavController().navigate(
                        DeviceCalibrationConfirmationFragmentDirections.actionDeviceCalibrationConfirmationFragmentToSetupCompleteFragment(
                            args.params.isComeFromSettings
                        )
                    )
                }
            )
        }
        binding.continueBtn.setOnClickListener {
            if (viewModel.currentCalibrationStateLiveData.value == null) {
                showSuccessDialog(
                    title = getString(R.string.warning),
                    message = getString(R.string.ome_knob_will_rotate),
                    onDismiss = {
                        viewModel.nextStep()
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
        viewModel.highAngle = args.params.highPosition
        viewModel.mediumAngle = args.params.medPosition
        viewModel.lowAngle = args.params.lowPosition

        binding.backIv.setOnClickListener {
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
        binding.knobView.apply {
            setOffPosition(args.params.offPosition)
            setHighPosition(args.params.highPosition)
            setMediumPosition(args.params.medPosition)
            setLowPosition(args.params.lowPosition)
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
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
            binding.subLabelTv.makeGone()
            binding.labelTv.text =
                getString(R.string.do_the_settings_below_match_your_stove_knob)

        }
        subscribe(viewModel.zoneLiveData) {
            binding.knobView.setStovePosition(it)
        }
        subscribe(viewModel.currentCalibrationStateLiveData) { currentStep ->
            currentStep?.let {
                binding.labelTv.text =
                    getString(R.string.calibration_confirmation_label, currentStep.name)


                binding.subLabelTv.makeVisible()
                binding.labelTv.text =
                    getString(R.string.calibration_confirmation_label, currentStep.name)
            }
        }

    }
}

@Parcelize
data class DeviceCalibrationConfirmationFragmentParams(
    val isComeFromSettings: Boolean = true,
    val offPosition: Float = 0f,
    val lowPosition: Float = 0f,
    val medPosition: Float = 0f,
    val highPosition: Float = 0f,
    val isDualKnob: Boolean = false,
    val rotateDir: Int = 0,
    val macAddr: String = ""
) : Parcelable
