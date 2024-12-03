package com.ome.app.presentation.dashboard.settings.add_knob.direction

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
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
            if(params.isEditMode)
                viewModel.updateDirection(onEnd = mainViewModel::getAllKnobs)
            else {
                viewModel.continueBtnClicked = true
                binding.continueBtn.startAnimation()
                if(!mainViewModel.webSocketManager.connected)
                    mainViewModel.connectToSocket(true)
                else {
                    lifecycleScope.launch {
//                        delay(3.seconds)
                        mainViewModel.socketConnected.emit(mainViewModel.webSocketManager.connected)
                    }
                }
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
    }


    override fun setupObserver() {
        super.setupObserver()
        mainViewModel.socketConnected.collectWithLifecycle {
            binding.continueBtn.revertAnimation()
            if(it) {
                if(!viewModel.continueBtnClicked) return@collectWithLifecycle
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
            }else onError("Socket connection failed.")
        }
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

@Parcelize
data class DirectionSelectionFragmentParams(
    val isComeFromSettings: Boolean = false,
    val isDualKnob: Boolean = false,
    val macAddress: String = "",
    val isEditMode: Boolean = false
) : Parcelable

