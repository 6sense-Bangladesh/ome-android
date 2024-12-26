package com.ome.app.presentation.dashboard.settings.add_knob.direction

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentDirectionSelectionBinding
import com.ome.app.domain.model.state.Rotation
import com.ome.app.domain.model.state.rotation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DirectionSelectionFragment :
    BaseFragment<DirectionSelectionViewModel, FragmentDirectionSelectionBinding>(
        FragmentDirectionSelectionBinding::inflate
    ) {
    override val viewModel: DirectionSelectionViewModel by viewModels()

    private val args by navArgs<DirectionSelectionFragmentArgs>()
    val params by lazy { args.params }


    override fun setupUI() {
        viewModel.macAddress = params.macAddress
        if(params.isEditMode) {
            mainViewModel.getKnobByMac(viewModel.macAddress)?.let {
                viewModel.calRequest = it.calibration.toSetCalibrationRequest()
                viewModel.calibrated = it.calibrated.orFalse()
                viewModel.clockwiseDir = it.calibration.rotationDir
                changeRotationState(it.calibration.rotationDir)
            }
        }else{
            mainViewModel.selectedDirection?.let {
                viewModel.clockwiseDir = it
                changeRotationState(it)
            }
        }
    }

    private fun changeRotationState(it: Int) {
        when (it.rotation) {
            Rotation.CLOCKWISE -> binding.clockWise.isChecked = true
            Rotation.COUNTER_CLOCKWISE -> binding.counterClockWise.isChecked = true
            else -> Unit
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setBounceClickListener {
            if(params.isEditMode)
                viewModel.updateDirection(onEnd = mainViewModel::getAllKnobs)
            else {
                mainViewModel.selectedDirection = viewModel.clockwiseDir
                navigateSafe(
                    DirectionSelectionFragmentDirections.actionDirectionSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = params.isComeFromSettings,
                            isDualKnob = params.isDualKnob,
                            rotateDir = viewModel.clockwiseDir,
                            macAddress = params.macAddress
                        )
                    )
                )
            }
        }
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked) {
                when (checkedId) {
                    binding.counterClockWise.id -> viewModel.clockwiseDir = Rotation.COUNTER_CLOCKWISE.value
                    binding.clockWise.id -> viewModel.clockwiseDir = Rotation.CLOCKWISE.value
                }
            }
        }
    }


    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle {
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if(params.isEditMode)
                    onBackPressed()
            }
        }
    }
}

@Keep
@Parcelize
data class DirectionSelectionFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isDualKnob: Boolean = false,
    val macAddress: String = "",
    val isEditMode: Boolean = false
) : Parcelable

