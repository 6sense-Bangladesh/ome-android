package com.ome.app.ui.dashboard.settings.add_knob.zone

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentZoneSelectionBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.navigate
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.ui.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.ui.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ZoneSelectionFragment :
    BaseFragment<ZoneSelectionViewModel, FragmentZoneSelectionBinding>(
        FragmentZoneSelectionBinding::inflate
    ) {
    override val viewModel: ZoneSelectionViewModel by viewModels()


    private val args by navArgs<ZoneSelectionFragmentArgs>()

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
            if (viewModel.zoneNumber == 2) {
                findNavController().navigate(
                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = args.params.isComeFromSettings,
                            zoneNumber = viewModel.zoneNumber,
                            isDualKnob = viewModel.isDualKnob,
                            macAddr =  args.params.macAddr
                        )
                    )
                )
            } else {
                Screens.DirectionSelection.navigate(
                    DirectionSelectionFragmentParams(
                        isComeFromSettings = args.params.isComeFromSettings,
                        zoneNumber = viewModel.zoneNumber,
                        isDualKnob = viewModel.isDualKnob,
                        macAddress = args.params.macAddr
                    )
                )
//                findNavController().navigate(
//                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDirectionSelectionFragment(
//                        DirectionSelectionFragmentParams(
//                            args.params.isComeFromSettings,
//                            viewModel.zoneNumber,
//                            viewModel.isDualKnob,
//                            args.params.macAddr
//                        )
//                    )
//                )
            }

        }
        binding.singleZoneRl.setOnClickListener {
            binding.dualZoneCoverIv.makeVisible()
            binding.singleZoneCoverIv.makeGone()
            viewModel.zoneNumber = 1
            viewModel.isDualKnob = false
            enableContinueButton()
        }
        binding.dualZoneCoverIv.setOnClickListener {
            binding.singleZoneCoverIv.makeVisible()
            binding.dualZoneCoverIv.makeGone()
            viewModel.zoneNumber = 2
            viewModel.isDualKnob = true
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


    override fun setupObserver() {
        super.setupObserver()
    }
}


@Parcelize
data class ZoneSelectionFragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddr: String = ""
) : Parcelable
