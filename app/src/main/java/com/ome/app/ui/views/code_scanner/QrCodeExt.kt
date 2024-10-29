package com.ome.app.ui.views.code_scanner

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// In your main activity or fragment
context(Fragment)
@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
suspend fun PreviewView.startQrScanner(): String = suspendCoroutine { continuation ->
    val cameraController = LifecycleCameraController(context)
    val barcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)
    cameraController.unbind() // Unbind any previous bindings
    var job: Job? = null
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        MlKitAnalyzer(
            listOf(barcodeScanner),
            ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(context)
        ) { result ->
            job?.cancel()
            job = lifecycleScope.launch{
                val qrData = withContext(Dispatchers.Default){
                    val barcodeResults = result?.getValue(barcodeScanner)
                    barcodeResults?.firstOrNull()?.rawValue
                }
                Log.d("TAG", "startQrScanner: $qrData")
                if (qrData.isNullOrEmpty())
                    overlay.clear()
                else{
                    barcodeScanner.close()
                    cameraController.clearImageAnalysisAnalyzer()
                    continuation.resume(qrData)
                }
            }
        }
    )
    cameraController.bindToLifecycle(this@Fragment)
    controller = cameraController
}

