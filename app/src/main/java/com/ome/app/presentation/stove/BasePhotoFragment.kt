package com.ome.app.presentation.stove

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.views.camera.CaptureDialogFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


abstract class BasePhotoFragment<VM : BasePhotoViewModel, VB : ViewBinding>(
    factory: (LayoutInflater) -> VB
) : BaseFragment<VM, VB>(factory) {

    abstract override val viewModel: VM

    private val chooseFromPhone =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                handleChooseFromPhoneResult(it)
            }
        }


    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if(result){
                viewModel.currentContentUri?.let {
                    handleTakeAPhotoResult(it)
                }
            }
        }



    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var isAllPermissionsGranted = false
            permissions.entries.forEach permissionLoop@{
                val isGranted = it.value
                if (!isGranted) {
                    isAllPermissionsGranted = false
                    return@permissionLoop
                } else {
                    isAllPermissionsGranted = true
                }
            }
            if (isAllPermissionsGranted) {
//                viewModel.loadingLiveData.postValue(true)
//                openCamera()
                CaptureDialogFragment().show(childFragmentManager, "capture")
            } else {
//                viewModel.loadingLiveData.postValue(false)
                onError(getString(R.string.permission_denied))
            }
        }

    private fun openCamera() {
        viewModel.currentFile = createImageFile()
        viewModel.currentFile?.let {
            viewModel.currentContentUri = FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.fileprovider", it)
            takePicture.launch(viewModel.currentContentUri)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            timeStamp,
            ".png",
            storageDir
        )
    }


    fun launchCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            val permissionArray = arrayOf(Manifest.permission.CAMERA)
//            viewModel.loadingLiveData.postValue(true)
            activityResultLauncher.launch(permissionArray)
        } else {
//            viewModel.loadingLiveData.postValue(true)
//            openCamera()
            CaptureDialogFragment().show(childFragmentManager, "capture")
        }
    }


    fun openPhoneGallery() = chooseFromPhone.launch("image/*")

    open fun handleTakeAPhotoResult(uri: Uri) {}

    open fun handleChooseFromPhoneResult(uri: Uri) {}

}
