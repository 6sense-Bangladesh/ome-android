package com.ome.app.ui.dashboard.settings.add_knob.wifi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentConnectToWifiBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConnectToWifiFragment : BaseFragment<ConnectToWifiViewModel, FragmentConnectToWifiBinding>(
    FragmentConnectToWifiBinding::inflate
) {
    override val viewModel: ConnectToWifiViewModel by viewModels()

//    private val args by navArgs<ConnectToWifiFragmentArgs>()
    private val args by lazy { Screens.ConnectToWifi.getData(arguments) }

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
        viewModel.isChangeWifiMode = args.isChangeWifiMode
        binding.connectBtn.setOnClickListener { checkPermission() }
        binding.manualSetupTv.setOnClickListener {
            findNavController().navigate(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToManualSetupFragment(
                    ManualSetupFragmentParams(
                        macAddrs = viewModel.macAddr,
                        isComeFromSettings = args.isComeFromSettings,
                        isChangeWifiMode = args.isChangeWifiMode
                    )
                )
            )
        }

        viewModel.initListeners()
        viewModel.macAddr = args.macAddrs
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

    override fun setupObserver() {
        super.setupObserver()
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
                    WifiListFragmentParams(
                        macAddrs = viewModel.macAddr,
                        isComeFromSettings = args.isComeFromSettings,
                        isChangeWifiMode = args.isChangeWifiMode
                    )
                )
            )
        }
    }
}

@Parcelize
data class ConnectToWifiParams(
    val isComeFromSettings: Boolean = true,
    val isChangeWifiMode: Boolean = false,
    val macAddrs: String = ""
) : Parcelable
