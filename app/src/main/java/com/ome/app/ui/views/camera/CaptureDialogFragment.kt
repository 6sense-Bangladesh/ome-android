package com.ome.app.ui.views.camera

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ome.app.R
import com.ome.app.databinding.FragmentCaptureBinding
import com.ome.app.ui.stove.StoveSetupPhotoViewModel
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.gone
import com.ome.app.utils.loge
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.show
import com.ome.app.utils.toBitmap
import com.ome.app.utils.toast
import com.ome.app.utils.tryGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CaptureDialogFragment(
    private val photoFile: (File) -> Unit = {}
) : DialogFragment() {
    private lateinit var binding: FragmentCaptureBinding
    private val viewModel: StoveSetupPhotoViewModel by activityViewModels()
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var photoAdapter: PhotoAdapter? = null
    private var defaultCameraFacing = CameraSelector.DEFAULT_BACK_CAMERA
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.processingCount = 0
        viewModel.clearList()
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_DialogWhenLarge_NoActionBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupUI() {
        setupCamera()
    }


    private fun setupListeners() {
        binding.apply {
            btnCapture.setBounceClickListener {
                MediaPlayer.create(requireContext(), R.raw.capture)?.apply {
                    start()
                    setOnCompletionListener(MediaPlayer::release)
                }
                takePhoto()
            }
            btnSave.setBounceClickListener {
                if(viewModel.isProcessing)
                    loadingLayout.show()
                else
                    onProcessingEnd()
            }
            btnBack.setBounceClickListener {
                viewModel.clearList()
                dismiss()
            }


            btnFlipCamera.setBounceClickListener {
                Log.d("CameraFacing", defaultCameraFacing.toString())
                defaultCameraFacing =
                    if (defaultCameraFacing == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    } else {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }

                try {
                    // Only bind use cases if we can query a camera with this orientation
                    setupCamera(defaultCameraFacing)
                } catch (exc: Exception) {
                    // Do nothing
                }
            }


        }
    }

    private fun setupObservers() {
        viewModel.photoThumbList.collectWithLifecycle{ lst ->
//            val photoList = lst.map { Uri.fromFile(it) }
            photoAdapter?.let {
                photoAdapter = PhotoAdapter(lst, viewModel)
                binding.photoListRv.swapAdapter(photoAdapter, true)
                photoAdapter?.onItemClickListener = { position ->
                    val imageFiles: List<File> = viewModel.photoList.value
                    val fullScreenImageDialog = ImageViewDialogFragment(imageFiles, position)
                    fullScreenImageDialog.show(childFragmentManager, "FullScreenImageDialog")
                }
            } ?: run {
                photoAdapter = PhotoAdapter(lst, viewModel)
                binding.photoListRv.adapter = photoAdapter
                photoAdapter?.onItemClickListener = { position ->
                    val imageFiles: List<File> = viewModel.photoList.value
                    val fullScreenImageDialog = ImageViewDialogFragment(imageFiles, position)
                    fullScreenImageDialog.show(childFragmentManager, "FullScreenImageDialog")
                }
            }
        }
    }

    private fun setupCamera(cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture
                .Builder()
                .setTargetRotation( tryGet { binding.root.display.rotation } ?:  Surface.ROTATION_0 )
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
//        viewModel.processingCount++
        viewModel.isProcessing = true
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
//        val photoFile = File(ContextCompat.getDataDir(requireContext()), "img_${System.currentTimeMillis()}.jpg")
        val photoFile = File(requireContext().cacheDir, "img_${System.currentTimeMillis()}.jpg")
        photoFile.absolutePath.loge("takePhoto")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch {
                        val bitmapTmp = photoFile.toBitmap(requireContext())
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addPhotoThumb(Bitmap.createScaledBitmap(bitmapTmp, 64, 64, true))
                        }
                        val maxImageSize = 1500f
                        val ratio = maxImageSize / bitmapTmp.width
                        val width = Math.round(ratio * bitmapTmp.width)
                        val height = Math.round(ratio * bitmapTmp.height)
                        val bitmapResized = withContext(Dispatchers.IO){
                            Bitmap.createScaledBitmap(bitmapTmp, width, height, true)
                        }
                        Log.d("TAG", "Photo capture succeeded: $photoFile $bitmapResized")


                        withContext(Dispatchers.IO){
                            FileOutputStream(photoFile).use { out ->
                                bitmapResized.compress(Bitmap.CompressFormat.JPEG, 50, out)
                            }
                        }
                        viewModel.addPhoto(photoFile)
                        viewModel.isProcessing = false
                        if(binding.loadingLayout.isVisible)
                            onProcessingEnd()
                    }

                }
            })
    }

    private fun onProcessingEnd(){
        binding.loadingLayout.gone()
        viewModel.photoList.value.firstOrNull()?.let(photoFile) ?: toast("Please capture an image")
        viewModel.clearList()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }
}