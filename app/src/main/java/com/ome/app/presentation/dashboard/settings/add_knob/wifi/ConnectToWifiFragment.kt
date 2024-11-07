package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentConnectToWifiBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

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
        viewModel.isChangeWifiMode = args.params.isEditMode
        viewModel.macAddrs = args.params.macAddrs

        binding.connectBtn.setOnClickListener { checkPermission() }
        binding.btnManual.setOnClickListener {
            findNavController().navigate(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToManualSetupFragment(
                    ManualSetupFragmentParams(
                        macAddrs = viewModel.macAddrs,
                        isEditMode = args.params.isEditMode,
                    )
                )
            )
        }

        viewModel.initListeners()
        viewModel.setupWifi()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
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

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.wifiConnectedFlow.collectWithLifecycle{
            binding.connectBtn.revertAnimation()
            findNavController().navigate(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToWifiListFragment(
                    WifiListFragmentParams(
                        macAddrs = viewModel.macAddrs,
                        isEditMode = viewModel.isChangeWifiMode,
                    )
                )
            )
        }
    }
}

@Parcelize
data class ConnectToWifiParams(
    val isEditMode: Boolean = false,
    val macAddrs: String = ""
) : Parcelable
