package com.ome.app.ui.dashboard.settings.add_knob.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.databinding.FragmentQrCodeScannerBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.views.code_scanner.*
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize


@AndroidEntryPoint
class QrCodeScannerFragment : BaseFragment<QrCodeScannerViewModel, FragmentQrCodeScannerBinding>(
    FragmentQrCodeScannerBinding::inflate
) {
    override val viewModel: QrCodeScannerViewModel by viewModels()

    private val args by navArgs<QrCodeScannerFragmentArgs>()

    private lateinit var codeScanner: CodeScanner

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initScanner()
            } else {
//                showErrorToast("Permission not granted")
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
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        viewModel.stovePosition = args.params.selectedIndex

        Handler(Looper.getMainLooper()).postDelayed({
            codeScanner = CodeScanner(requireContext(), binding.scannerView)
            checkPermission()
        }, 200)

        onDismissErrorDialog = {
            codeScanner.startPreview()
        }

    }

    private fun initScanner() {
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                binding.loadingLayout.container.visibility = View.VISIBLE
                viewModel.checkStoveOwnership(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.scannerView.setOnClickListener { codeScanner.startPreview() }
        codeScanner.startPreview()

    }

    private fun checkPermission() = if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        initScanner()
    } else {
        requestPermissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.isKnobAddedLiveData) {
            if (it) {
                viewModel.loadingLiveData.postValue(false)
            } else {
                viewModel.addNewKnob()
            }
        }
        subscribe(viewModel.knobCreatedLiveData) {
            viewModel.loadingLiveData.postValue(false)
        }
        subscribe(viewModel.loadingLiveData) {
            if (it) {
                binding.loadingLayout.container.visibility = View.VISIBLE
            } else {
                binding.loadingLayout.container.visibility = View.GONE
            }
        }
    }
}

@Parcelize
data class QrCodeScannerParams(
    val selectedIndex: Int = 0
) : Parcelable
