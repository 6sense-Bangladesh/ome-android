package com.ome.app.presentation.dashboard.settings.add_knob.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentQrCodeScannerBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.presentation.views.code_scanner.startQrScanner
import com.ome.app.utils.changeVisibility
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class QrCodeScannerFragment : BaseFragment<QrCodeScannerViewModel, FragmentQrCodeScannerBinding>(
    FragmentQrCodeScannerBinding::inflate
) {
    override val viewModel: QrCodeScannerViewModel by viewModels()

    private val args by navArgs<QrCodeScannerFragmentArgs>()
    val params by lazy { args.params }

//    private lateinit var codeScanner: CodeScanner

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                Unit
             else
                onError(getString(R.string.permission_not_granted))
        }

    override fun setupUI() {
        checkPermission()
        viewModel.stovePosition = params.selectedKnobPosition
        initQrCodeScanner()
    }

    private fun initQrCodeScanner() {
        lifecycleScope.launch {
            val qr = binding.previewView.startQrScanner()
            viewModel.checkKnobOwnership(qr)
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        onDismissErrorDialog = {
            initQrCodeScanner()
        }
    }

    private fun checkPermission() =
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            Unit
        else
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)


    override fun onPause() {
//        codeScanner.releaseResources()
        super.onPause()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.isKnobAddedFlow.collectWithLifecycle{
            mainViewModel.getUserInfo()
            viewModel.macAddress?.let { mac ->
                navigateSafe(QrCodeScannerFragmentDirections.actionQrCodeScannerFragmentToConnectToWifiFragment(
                    ConnectToWifiParams(macAddrs = mac)
                )).also {
                    viewModel.isKnobAddedFlow.value = null
                } ?: onError(getString(R.string.scan_again))
            } ?: onError(getString(R.string.scan_again))
        }
        viewModel.loadingFlow.collectWithLifecycle{
            binding.loadingLayout.container.changeVisibility(it)
        }
    }
}

@Keep
@Parcelize
data class QrCodeScannerParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
