package com.ome.app.ui.dashboard.settings.add_knob.wifi

import android.os.Bundle
import android.os.Parcelable
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
import kotlinx.android.parcel.Parcelize

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

        viewModel.macAddr = args.params.macAddrs
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.wifiNetworksListLiveData) {
            binding.connectBtn.revertAnimation()
            findNavController().navigate(
                ManualSetupFragmentDirections.actionManualSetupFragmentToWifiListFragment(
                    WifiListFragmentParams(
                        isComeFromSettings = args.params.isComeFromSettings,
                        macAddrs = args.params.macAddrs
                    )
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

@Parcelize
data class ManualSetupFragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
