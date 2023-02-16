package com.ome.app.ui.dashboard.settings.add_knob.wifi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentConnectToWifiBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class ConnectToWifiFragment : BaseFragment<ConnectToWifiViewModel, FragmentConnectToWifiBinding>(
    FragmentConnectToWifiBinding::inflate
) {
    override val viewModel: ConnectToWifiViewModel by viewModels()

    private val args by navArgs<ConnectToWifiFragmentArgs>()


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.connectBtn.startAnimation()
                viewModel.connectToWifi()

            } else {
                onError(getString(R.string.permission_not_granted))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.connectBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }
        binding.connectBtn.setOnClickListener { checkPermission() }
        binding.manualSetupTv.setOnClickListener {
            findNavController().navigate(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToManualSetupFragment(
                    macAddr = viewModel.macAddr
                )
            )
        }

        viewModel.initListeners()
        viewModel.macAddr = args.macAddr
        viewModel.setupWifi()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.connectBtn.startAnimation()
            viewModel.connectToWifi()
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.loadingLiveData) {
            if (it) {
                binding.connectBtn.startAnimation()
            } else {
                binding.connectBtn.revertAnimation()
            }
        }
        subscribe(viewModel.wifiNetworksListLiveData) {
            findNavController().navigate(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToWifiListFragment(
                    macAddr = viewModel.macAddr
                )
            )
        }
    }
}
