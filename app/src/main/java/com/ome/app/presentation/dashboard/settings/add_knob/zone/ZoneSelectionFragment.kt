package com.ome.app.presentation.dashboard.settings.add_knob.zone

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentZoneSelectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.DeviceCalibrationFragmentParams
import com.ome.app.presentation.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ZoneSelectionFragment :
    BaseFragment<ZoneSelectionViewModel, FragmentZoneSelectionBinding>(
        FragmentZoneSelectionBinding::inflate
    ) {
    override val viewModel: ZoneSelectionViewModel by viewModels()


    private val args by navArgs<ZoneSelectionFragmentArgs>()

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setBounceClickListener {
            if (viewModel.isDualKnob) {
                binding.continueBtn.startAnimation()
                mainViewModel.connectToSocket()
            } else {
                navigateSafe(
                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDirectionSelectionFragment(
                        DirectionSelectionFragmentParams(
                            isComeFromSettings = args.params.isComeFromSettings,
                            isDualKnob = viewModel.isDualKnob,
                            macAddress = args.params.macAddrs
                        )
                    )
                )
            }

        }
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked) {
                when (checkedId) {
                    binding.singleZone.id -> viewModel.isDualKnob = false
                    binding.dualZone.id -> viewModel.isDualKnob = true
                }
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        mainViewModel.socketConnected.collectWithLifecycle {
            binding.continueBtn.revertAnimation()
            navigateSafe(
                ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDeviceCalibrationFragment(
                    DeviceCalibrationFragmentParams(
                        isComeFromSettings = args.params.isComeFromSettings,
                        isDualKnob = viewModel.isDualKnob,
                        macAddress =  args.params.macAddrs
                    )
                )
            )
        }
    }

}


@Parcelize
data class ZoneSelectionFragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
