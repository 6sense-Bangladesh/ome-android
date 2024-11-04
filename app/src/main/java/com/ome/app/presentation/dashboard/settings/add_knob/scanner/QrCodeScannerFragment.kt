package com.ome.app.presentation.dashboard.settings.add_knob.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentQrCodeScannerBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.presentation.views.code_scanner.startQrScanner
import com.ome.app.utils.changeVisibility
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class QrCodeScannerFragment : BaseFragment<QrCodeScannerViewModel, FragmentQrCodeScannerBinding>(
    FragmentQrCodeScannerBinding::inflate
) {
    override val viewModel: QrCodeScannerViewModel by viewModels()

    private val args by navArgs<QrCodeScannerFragmentArgs>()

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
        viewModel.stovePosition = args.params.selectedKnobPosition
        initQrCodeScanner()
    }

    private fun initQrCodeScanner() {
        lifecycleScope.launch {
            val qr = binding.previewView.startQrScanner()
            viewModel.checkStoveOwnership(qr)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.backIv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
//        binding.backIv.setOnClickListener { findNavController().popBackStack() }


//        Handler(Looper.getMainLooper()).postDelayed({
////            codeScanner = CodeScanner(requireContext(), binding.scannerView)
//            checkPermission()
//        }, 200)


    }

    override fun setupListener() {
        binding.apply {
            topAppBar.setNavigationOnClickListener(::onBackPressed)
        }
        onDismissErrorDialog = {
            initQrCodeScanner()
        }
    }

//    private fun initScanner() {
//        codeScanner.camera = CodeScanner.CAMERA_BACK
//        codeScanner.formats = CodeScanner.ALL_FORMATS
//
//        codeScanner.autoFocusMode = AutoFocusMode.SAFE
//        codeScanner.scanMode = ScanMode.SINGLE
//        codeScanner.isAutoFocusEnabled = true
//        codeScanner.isFlashEnabled = false
//
//        codeScanner.decodeCallback = DecodeCallback {
//            activity?.runOnUiThread {
//                binding.loadingLayout.container.visibility = View.VISIBLE
//                viewModel.checkStoveOwnership(it.text)
//            }
//        }
//        codeScanner.errorCallback = ErrorCallback {
//            activity?.runOnUiThread {
//                Toast.makeText(
//                    requireContext(), "Camera initialization error: ${it.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//        binding.scannerView.setOnClickListener { codeScanner.startPreview() }
//        codeScanner.startPreview()
//
//    }

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
        subscribe(viewModel.isKnobAddedLiveData) {
            if (it) {
                viewModel.macAddress?.let { mac ->
                    findNavController().navigate(
                        QrCodeScannerFragmentDirections.actionQrCodeScannerFragmentToConnectToWifiFragment(
                            ConnectToWifiParams(macAddrs = mac)
                        )
                    )
                } ?: onError(getString(R.string.scan_again))
            } else {
                viewModel.addNewKnob()
            }
        }
        subscribe(viewModel.knobCreatedLiveData) {
            mainViewModel.getUserInfo()
            viewModel.macAddress?.let { mac ->
                findNavController().navigate(
                    QrCodeScannerFragmentDirections.actionQrCodeScannerFragmentToConnectToWifiFragment(
                        ConnectToWifiParams(macAddrs = mac)
                    )
                )
            }
        }
        viewModel.loadingFlow.collectWithLifecycle{
            binding.loadingLayout.container.changeVisibility(it, useGone = true)
        }
    }
}

@Parcelize
data class QrCodeScannerParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
