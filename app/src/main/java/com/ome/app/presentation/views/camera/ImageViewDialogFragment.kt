package com.ome.app.presentation.views.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ome.app.databinding.DialogFullscreenImageBinding
import com.ome.app.utils.changeVisibility
import com.ome.app.utils.loadDrawable
import com.ome.app.utils.setBounceClickListener

class ImageViewDialogFragment(
    private val imageLinksOrFiles: List<Any> = emptyList(),
    startPosition: Int = 0
) : DialogFragment() {
    private lateinit var binding: DialogFullscreenImageBinding
    private var currentPosition = startPosition

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted)
//            context.saveImage(imageLinksOrFiles.getOrNull(currentPosition))
//        else
//            toast("Storage permission is denied")
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_NoActionBar_TranslucentDecor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        loadImage(currentPosition)
        updateNavigationButtons()
    }

    private fun setupListeners() {
        binding.btnClose.setBounceClickListener {
            dismiss()
        }

        binding.btnNext.setBounceClickListener {
            if (currentPosition < imageLinksOrFiles.size - 1) {
                currentPosition++
                loadImage(currentPosition)
            }
        }

        binding.btnPrevious.setBounceClickListener {
            if (currentPosition > 0) {
                currentPosition--
                loadImage(currentPosition)
            }
        }

//        binding.btnDownload.setBounceClickListener {
//            val permissionCheckStorage = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//                context.saveImage(imageLinksOrFiles.getOrNull(currentPosition))
//            else if (permissionCheckStorage != PackageManager.PERMISSION_GRANTED)
//                askStoragePermission()
//            else
//                context.saveImage(imageLinksOrFiles.getOrNull(currentPosition))
//        }
    }

    private fun loadImage(position: Int) {
        binding.imageViewFullScreen.loadDrawable(imageLinksOrFiles.getOrNull(position))
        updateNavigationButtons()
    }

    private fun updateNavigationButtons() {
        binding.btnNext.changeVisibility(currentPosition < imageLinksOrFiles.size - 1)
        binding.btnPrevious.changeVisibility(currentPosition > 0)
    }
//
//    private fun askStoragePermission() {
//        // This is only necessary for API level < 29 (A10)
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED
//            ) requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//    }
}
