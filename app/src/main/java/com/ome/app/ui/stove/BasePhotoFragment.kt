package com.ome.app.ui.stove

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.ome.Ome.BuildConfig
import com.ome.Ome.R
import com.ome.app.ui.base.BaseFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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
                viewModel.loadingLiveData.postValue(true)
                openCamera()
            } else {
                viewModel.loadingLiveData.postValue(false)
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
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permissionArray =
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            viewModel.loadingLiveData.postValue(true)
            activityResultLauncher.launch(permissionArray)
        } else {
            viewModel.loadingLiveData.postValue(true)
            openCamera()
        }
    }


    fun openPhoneGallery() = chooseFromPhone.launch("image/*")

    open fun handleTakeAPhotoResult(bitmap: Uri) {}

    open fun handleChooseFromPhoneResult(uri: Uri) {}

}
