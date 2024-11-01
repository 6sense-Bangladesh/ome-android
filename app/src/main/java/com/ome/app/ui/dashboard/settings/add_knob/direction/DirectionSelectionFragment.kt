package com.ome.app.ui.dashboard.settings.add_knob.direction

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentDirectionSelectionBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.ui.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class DirectionSelectionFragment :
    BaseFragment<DirectionSelectionViewModel, FragmentDirectionSelectionBinding>(
        FragmentDirectionSelectionBinding::inflate
    ) {
    override val viewModel: DirectionSelectionViewModel by viewModels()

//    private val args by navArgs<DirectionSelectionFragmentArgs>()
    private val args by lazy { Screens.DirectionSelection.getData(arguments) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.macAddress = args.macAddress
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
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.continueBtn.setOnClickListener {
            if(args.isChangeMode){
                viewModel.updateDirection()
            }else {
                findNavController().navigate(
                    DirectionSelectionFragmentDirections.actionDirectionSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = args.isComeFromSettings,
                            zoneNumber = args.zoneNumber,
                            isDualKnob = args.isDualKnob,
                            rotateDir = viewModel.clockwiseDir,
                            macAddr = args.macAddress
                        )
                    )
                )
            }
        }
        binding.counterClockWiseRl.setOnClickListener {
            binding.clockWiseCoverIv.makeVisible()
            binding.counterClockWiseCoverIv.makeGone()
            viewModel.clockwiseDir = -1
            enableContinueButton()
        }
        binding.clockWiseRl.setOnClickListener {
            binding.counterClockWiseCoverIv.makeVisible()
            binding.clockWiseCoverIv.makeGone()
            viewModel.clockwiseDir = 1
            enableContinueButton()
        }

    }

    private fun enableContinueButton() {
        binding.continueBtn.isEnabled = true
//        ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
//            ?.let {
//                binding.continueBtn.drawableBackground = it
//            }
//        binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
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
    val isComeFromSettings: Boolean = true,
    val zoneNumber: Int = 0,
    val isDualKnob: Boolean = false,
    val macAddress: String = "",
    val isChangeMode: Boolean = false
) : Parcelable

