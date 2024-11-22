package com.ome.app.presentation.stove

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.ome.app.R
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.views.camera.CaptureDialogFragment


abstract class BasePhotoFragment<VB : ViewBinding>(
    factory: (LayoutInflater) -> VB
) : BaseFragment<PhotoCaptureViewModel, VB>(factory) {

    override val viewModel: PhotoCaptureViewModel by activityViewModels()

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted)
            CaptureDialogFragment().show(childFragmentManager, "capture")
        else
            onError(getString(R.string.permission_denied))
    }

    private var galleryLauncher4photo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { handleChooseFromPhoneResult(it) }
    }

    private val photoPicker = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

    fun launchCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            CaptureDialogFragment().show(childFragmentManager, "capture")
        }
    }

    fun openPhoneGallery() = galleryLauncher4photo.launch(photoPicker)

    open fun handleChooseFromPhoneResult(uri: Uri) {}

}
