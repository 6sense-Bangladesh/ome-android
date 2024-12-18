package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentWifiListBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.NetworkItemAdapter
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class WifiListFragment : BaseFragment<WifiListViewModel, FragmentWifiListBinding>(
    FragmentWifiListBinding::inflate
) {
    override val viewModel: WifiListViewModel by viewModels()

    private val args by navArgs<WifiListFragmentArgs>()
    val params by lazy { args.params }

    private val adapter by lazy {
        NetworkItemAdapter(onClick = {
            navigateSafe(
                WifiListFragmentDirections.actionWifiListFragmentToConnectToWifiPasswordFragment(
                    ConnectToWifiPasswordParams(
                        ssid = it.ssid, securityType = it.securityType,
                        macAddr = viewModel.macAddr, isEditMode = params.isEditMode
                    )
                )
            )
        })
    }

    override fun setupUI() {
        viewModel.macAddr = params.macAddrs
        binding.recyclerView.adapter = adapter

    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.scanAgainBtn.setBounceClickListener {
            binding.scanAgainBtn.startAnimation()
            viewModel.getNetworks()
        }
        viewModel.socketManager.onSocketConnect = {
            if(!it){
                lifecycleScope.launch {
                    mainViewModel.socketError.emit(Unit)
                }
            }
        }
    }


    override fun setupObserver() {
        super.setupObserver()
        viewModel.wifiNetworksList.collectWithLifecycle {
            adapter.submitList(it)
            binding.scanAgainBtn.revertAnimation()
            binding.notFoundLayout.changeVisibility(it.isEmpty())
        }
    }
}
@Keep
@Parcelize
data class WifiListFragmentParams(
    val isEditMode: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
