package com.ome.app.ui.dashboard.settings.add_knob.direction

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentDirectionSelectionBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class DirectionSelectionFragment :
    BaseFragment<DirectionSelectionViewModel, FragmentDirectionSelectionBinding>(
        FragmentDirectionSelectionBinding::inflate
    ) {
    override val viewModel: DirectionSelectionViewModel by viewModels()

    private val args by navArgs<DirectionSelectionFragmentArgs>()

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
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                DirectionSelectionFragmentDirections.actionDirectionSelectionFragmentToDeviceCalibrationFragment(
                    DeviceCalibrationFragmentParams(
                        isComeFromSettings = args.params.isComeFromSettings,
                        zoneNumber = args.params.zoneNumber,
                        isDualKnob = args.params.isDualKnob,
                        rotateDir = viewModel.clockwiseDir,
                        macAddr = args.params.macAddress
                    )
                )
            )
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
        ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
            ?.let {
                binding.continueBtn.drawableBackground = it
            }
        binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
    }


    override fun observeLiveData() {
        super.observeLiveData()
    }
}

@Parcelize
data class DirectionSelectionFragmentParams(
    val isComeFromSettings: Boolean = true,
    val zoneNumber: Int = 0,
    val isDualKnob: Boolean = false,
    val macAddress: String = ""
) : Parcelable

