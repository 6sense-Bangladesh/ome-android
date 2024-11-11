package com.ome.app.presentation.dashboard.settings.add_knob.zone

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentZoneSelectionBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.toast
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
                toast("Under Development")
//                navigateSafe(
//                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDeviceCalibrationFragment(
//                        DeviceCalibrationFragmentParams(
//                            isComeFromSettings = args.params.isComeFromSettings,
//                            zoneNumber = viewModel.zoneNumber,
//                            isDualKnob = viewModel.isDualKnob,
//                            macAddr =  args.params.macAddrs
//                        )
//                    )
//                )
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
//        binding.singleZoneRl.setOnClickListener {
//            binding.dualZoneCoverIv.makeVisible()
//            binding.singleZoneCoverIv.makeGone()
//            viewModel.zoneNumber = 1
//            viewModel.isDualKnob = false
//        }
//        binding.dualZoneCoverIv.setOnClickListener {
//            binding.singleZoneCoverIv.makeVisible()
//            binding.dualZoneCoverIv.makeGone()
//            viewModel.zoneNumber = 2
//            viewModel.isDualKnob = true
//        }
    }

}


@Parcelize
data class ZoneSelectionFragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
