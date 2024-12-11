package com.ome.app.presentation.dashboard.settings.add_knob.zone

import android.os.Parcelable
import androidx.annotation.Keep
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
    val params by lazy { args.params }

    override fun setupUI() {
        mainViewModel.selectedDualZone?.let {
            if(it)
                binding.dualZone.isChecked = true
            else
                binding.singleZone.isChecked = true

        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setBounceClickListener {
            mainViewModel.selectedDualZone = viewModel.isDualKnob
            if (viewModel.isDualKnob) {
                navigateSafe(
                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = params.isComeFromSettings,
                            isDualKnob = viewModel.isDualKnob,
                            macAddress = params.macAddrs
                        )
                    )
                )
//                viewModel.continueBtnClicked = true
//                binding.continueBtn.startAnimation()
//                if(!mainViewModel.webSocketManager.connected)
//                    mainViewModel.connectToSocket(true)
//                else {
//                    lifecycleScope.launch {
////                        delay(3.seconds)
//                        mainViewModel.socketConnected.emit(mainViewModel.webSocketManager.connected)
//                    }
//                }
            } else {
                navigateSafe(
                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDirectionSelectionFragment(
                        DirectionSelectionFragmentParams(
                            isComeFromSettings = params.isComeFromSettings,
                            isDualKnob = viewModel.isDualKnob,
                            macAddress = params.macAddrs
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
            if(it) {
                if(!viewModel.continueBtnClicked) return@collectWithLifecycle
                navigateSafe(
                    ZoneSelectionFragmentDirections.actionZoneSelectionFragmentToDeviceCalibrationFragment(
                        DeviceCalibrationFragmentParams(
                            isComeFromSettings = params.isComeFromSettings,
                            isDualKnob = viewModel.isDualKnob,
                            macAddress = params.macAddrs
                        )
                    )
                )
            }else onError("Socket connection failed.")
        }
    }

}


@Keep
@Parcelize
data class ZoneSelectionFragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
