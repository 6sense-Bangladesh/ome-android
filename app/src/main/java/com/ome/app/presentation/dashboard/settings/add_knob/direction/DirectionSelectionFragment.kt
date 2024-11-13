package com.ome.app.presentation.dashboard.settings.add_knob.direction

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentDirectionSelectionBinding
import com.ome.app.domain.model.state.Rotation
import com.ome.app.domain.model.state.rotation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.orFalse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DirectionSelectionFragment :
    BaseFragment<DirectionSelectionViewModel, FragmentDirectionSelectionBinding>(
        FragmentDirectionSelectionBinding::inflate
    ) {
    override val viewModel: DirectionSelectionViewModel by viewModels()

    private val args by navArgs<DirectionSelectionFragmentArgs>()


    override fun setupUI() {
        viewModel.macAddress = args.params.macAddress
        mainViewModel.getKnobByMac(viewModel.macAddress)?.let {
            viewModel.calRequest = it.calibration.toSetCalibrationRequest()
            viewModel.calibrated = it.calibrated.orFalse()
            viewModel.clockwiseDir = it.calibration.rotationDir
            when(it.calibration.rotationDir.rotation){
                Rotation.CLOCKWISE -> binding.clockWise.isChecked = true
                Rotation.COUNTER_CLOCKWISE -> binding.counterClockWise.isChecked = true
                else -> Unit
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setOnClickListener {
            if(args.params.isEditMode){
                viewModel.updateDirection(onEnd = mainViewModel::getAllKnobs)
            }else {
                navigateSafe(
                    DirectionSelectionFragmentDirections.actionDirectionSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = args.params.isComeFromSettings,
                            isDualKnob = args.params.isDualKnob,
                            rotateDir = viewModel.clockwiseDir,
                            macAddr = args.params.macAddress
                        )
                    )
                )
            }
        }
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked) {
                when (checkedId) {
                    binding.counterClockWise.id -> viewModel.clockwiseDir = -1
                    binding.clockWise.id -> viewModel.clockwiseDir = 1
                }
            }
        }
//        binding.counterClockWiseRl.setOnClickListener {
//            binding.clockWiseCoverIv.makeVisible()
//            binding.counterClockWiseCoverIv.makeGone()
//            viewModel.clockwiseDir = -1
//            enableContinueButton()
//        }
//        binding.clockWiseRl.setOnClickListener {
//            binding.counterClockWiseCoverIv.makeVisible()
//            binding.clockWiseCoverIv.makeGone()
//            viewModel.clockwiseDir = 1
//            enableContinueButton()
//        }

    }


    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle {
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                onBackPressed()
            }
        }
    }
}

@Parcelize
data class DirectionSelectionFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isDualKnob: Boolean = false,
    val macAddress: String = "",
    val isEditMode: Boolean = false
) : Parcelable

