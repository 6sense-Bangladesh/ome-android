package com.ome.app.presentation.dashboard.settings.add_knob.direction

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentDirectionSelectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DirectionSelectionFragment :
    BaseFragment<DirectionSelectionViewModel, FragmentDirectionSelectionBinding>(
        FragmentDirectionSelectionBinding::inflate
    ) {
    override val viewModel: DirectionSelectionViewModel by viewModels()

    private val args by navArgs<DirectionSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.macAddress = args.params.macAddress
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setOnClickListener {
            if(args.params.isEditMode){
                viewModel.updateDirection()
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
            if (!it) onBackPressed()
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

