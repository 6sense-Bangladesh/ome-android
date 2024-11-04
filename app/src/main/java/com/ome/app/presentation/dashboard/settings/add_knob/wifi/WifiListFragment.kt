package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.databinding.FragmentWifiListBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.recycler.RecyclerDelegationAdapter
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemAdapter
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class WifiListFragment : BaseFragment<WifiListViewModel, FragmentWifiListBinding>(
    FragmentWifiListBinding::inflate
) {
    override val viewModel: WifiListViewModel by viewModels()

    private val args by navArgs<WifiListFragmentArgs>()

    private val adapter by lazy {
        RecyclerDelegationAdapter(requireContext()).apply {
            addDelegate(NetworkItemAdapter(requireContext()) { item ->
                findNavController().navigate(
                    WifiListFragmentDirections.actionWifiListFragmentToConnectToWifiPasswordFragment(
                        ConnectToWifiPasswordParams(
                            ssid = item.ssid,
                            securityType = item.securityType,
                            macAddr = viewModel.macAddr,
                            isEditMode = args.params.isEditMode
                        )
                    )
                )
            })
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.scanAgainBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }
        binding.scanAgainBtn.setOnClickListener {
            binding.scanAgainBtn.startAnimation()
            viewModel.sendMessage(KnobSocketMessage.GET_NETWORKS)
        }
        viewModel.macAddr = args.params.macAddrs

        binding.recyclerView.adapter = adapter

    }


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.wifiNetworksListLiveData) {
            binding.scanAgainBtn.revertAnimation()
            adapter.setItems(it)
        }
        subscribe(viewModel.loadingLiveData) {
            if (it) {
                binding.scanAgainBtn.startAnimation()
            } else {
                binding.scanAgainBtn.revertAnimation()
            }
        }

    }
}

@Parcelize
data class WifiListFragmentParams(
    val isEditMode: Boolean = true,
    val macAddrs: String = ""
) : Parcelable
