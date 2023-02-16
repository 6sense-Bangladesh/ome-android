package com.ome.app.ui.dashboard.settings.add_knob.wifi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentManualSetupBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class ManualSetupFragment : BaseFragment<ManualSetupViewModel, FragmentManualSetupBinding>(
    FragmentManualSetupBinding::inflate
) {
    override val viewModel: ManualSetupViewModel by viewModels()


    private val args by navArgs<ManualSetupFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.connectBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }
        binding.connectBtn.setOnClickListener {
            if (viewModel.isConnectedToKnobHotspot()) {
                binding.connectBtn.startAnimation()
                viewModel.connectToSocket()
            } else {
                onError(
                    getString(
                        R.string.manual_connection_to_hotspot_error,
                        viewModel.wifiHandler.omeKnobSSID,
                        viewModel.wifiHandler.inirvKnobSSID
                    )
                )
            }
        }
        viewModel.initListeners()

        viewModel.macAddr = args.macAddr
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.wifiNetworksListLiveData) {
            binding.connectBtn.revertAnimation()
            findNavController().navigate(
                ManualSetupFragmentDirections.actionManualSetupFragmentToWifiListFragment(
                    viewModel.macAddr
                )
            )
        }

        subscribe(viewModel.loadingLiveData) {
            if (it) {
                binding.connectBtn.startAnimation()
            } else {
                binding.connectBtn.revertAnimation()
            }

        }
    }
}
