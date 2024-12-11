package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentManualSetupBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
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
    val params by lazy { args.params }

    override fun setupUI() {
        viewModel.macAddr = params.macAddrs
        viewModel.wifiHandler.setup(viewModel.macAddr).let {
            binding.mac1.text = it.first
            binding.mac2.text = it.second
        }
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
            navigateSafe(
                ManualSetupFragmentDirections.actionManualSetupFragmentToWifiListFragment(
                    WifiListFragmentParams(
                        isEditMode = params.isEditMode,
                        macAddrs = params.macAddrs
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

@Keep
@Parcelize
data class ManualSetupFragmentParams(
    val isEditMode: Boolean = false,
    val macAddrs: String = ""
) : Parcelable
