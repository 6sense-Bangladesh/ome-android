package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentManualSetupBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

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
            lifecycleScope.launch {
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
        }
        viewModel.initListeners()

        viewModel.macAddr = args.params.macAddrs
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.wifiNetworksListLiveData) {
            binding.connectBtn.revertAnimation()
            findNavController().navigate(
                ManualSetupFragmentDirections.actionManualSetupFragmentToWifiListFragment(
                    WifiListFragmentParams(
                        isEditMode = args.params.isEditMode,
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
    val isEditMode: Boolean = false,
    val macAddrs: String = ""
) : Parcelable
