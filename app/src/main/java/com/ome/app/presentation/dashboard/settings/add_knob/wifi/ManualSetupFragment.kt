package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentManualSetupBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
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
        viewModel.networkManager.setup(viewModel.macAddr).let {
            binding.mac1.text = it.first
            binding.mac2.text = it.second
        }
        viewModel.initListeners()
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.connectBtn.setBounceClickListener {
            lifecycleScope.launch {
                binding.connectBtn.startAnimation()
                if(viewModel.networkManager.isWifiEnabled())
                    viewModel.connectToSocket()
                else
                    onError("Please turn on your Wi-Fi connection.")
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.wifiConnectedFlow.collectWithLifecycle{
            binding.connectBtn.revertAnimation()
            if(it){
                navigateSafe(
                    ManualSetupFragmentDirections.actionManualSetupFragmentToWifiListFragment(
                        WifiListFragmentParams(
                            isEditMode = params.isEditMode,
                            macAddrs = params.macAddrs
                        )
                    )
                )
            } else onError(getString(R.string.manual_connection_to_error))
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
