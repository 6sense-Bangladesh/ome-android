package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentConnectToWifiBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConnectToWifiFragment : BaseFragment<ConnectToWifiViewModel, FragmentConnectToWifiBinding>(
    FragmentConnectToWifiBinding::inflate
) {
    override val viewModel: ConnectToWifiViewModel by viewModels()

    private val args by navArgs<ConnectToWifiFragmentArgs>()
    val params by lazy { args.params }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val permissionDenied = permissions.any { !it.value }
            if (permissionDenied) {
                binding.connectBtn.startAnimation()
                viewModel.connectToWifi()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isChangeWifiMode = params.isEditMode
        viewModel.macAddrs = params.macAddrs

        binding.connectBtn.setOnClickListener { checkPermission() }
        binding.btnManual.setOnClickListener {
            navigateSafe(
                ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToManualSetupFragment(
                    ManualSetupFragmentParams(
                        macAddrs = viewModel.macAddrs,
                        isEditMode = params.isEditMode,
                    )
                )
            )
        }

        viewModel.initListeners()
        viewModel.setupWifi()
    }

    private fun checkPermission() {
        val permissions = buildList {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // Android 13+ requires NEARBY_WIFI_DEVICES
                    add(Manifest.permission.NEARBY_WIFI_DEVICES)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // Android 10 to 12 requires ACCESS_FINE_LOCATION
                    add(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                else -> {
                    // Android 9 and below require both FINE and COARSE location permissions
                    add(Manifest.permission.ACCESS_FINE_LOCATION)
                    add(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }

        // Filter out already granted permissions
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        // Launch permission request for missing permissions
        if (missingPermissions.isNotEmpty())
            permissionLauncher.launch(missingPermissions.toTypedArray())
        else {
            binding.connectBtn.startAnimation()
            viewModel.connectToWifi()
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        onDismissErrorDialog = {
            binding.connectBtn.revertAnimation()
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.wifiConnectedFlow.collectWithLifecycle{
            binding.connectBtn.revertAnimation()
            navigateSafe(
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
