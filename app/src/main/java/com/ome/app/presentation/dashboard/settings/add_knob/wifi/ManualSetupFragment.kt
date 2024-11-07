package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentManualSetupBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ManualSetupFragment : BaseFragment<ManualSetupViewModel, FragmentManualSetupBinding>(
    FragmentManualSetupBinding::inflate
) {
    override val viewModel: ManualSetupViewModel by viewModels()


    private val args by navArgs<ManualSetupFragmentArgs>()

    override fun setupUI() {
        viewModel.macAddr = args.params.macAddrs
    }

    override fun setupListener() {
        viewModel.initListeners()
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.connectBtn.setOnClickListener {
            lifecycleScope.launch {
                if (viewModel.isConnectedToKnobHotspot()) {
                    binding.connectBtn.startAnimation()
                    viewModel.connectToSocket()
                } else {
                    onError(getString(
                            R.string.manual_connection_to_hotspot_error,
                            viewModel.wifiHandler.omeKnobSSID,
                            viewModel.wifiHandler.inirvKnobSSID
                        ))
                }
            }
        }

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.wifiConnectedFlow.collectWithLifecycle{
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
