package com.ome.app.ui.stove

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentStoveSetupPhotoBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.getRealPathFromUri
import com.ome.app.utils.savePhotoToExternalStorage
import com.ome.app.utils.subscribe
import com.ome.app.utils.withDelay
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.util.*


@AndroidEntryPoint
class StoveSetupPhotoFragment :
    BaseFragment<StoveSetupPhotoViewModel, FragmentStoveSetupPhotoBinding>(
        FragmentStoveSetupPhotoBinding::inflate
    ) {

    override val viewModel: StoveSetupPhotoViewModel by viewModels()

    private val args by navArgs<StoveSetupPhotoFragmentArgs>()


    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            viewModel.fileName = UUID.randomUUID().toString()
            val uri = requireActivity().savePhotoToExternalStorage(viewModel.fileName, it)
            uri?.let {
                val filePath = uri.getRealPathFromUri(requireContext())
                filePath?.let { path ->
                    binding.shaftIv.setImageURI(uri)
                    viewModel.currentContentUri = uri
                    viewModel.uploadImage(File(path))
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        viewModel.currentContentUri?.let {
            binding.shaftIv.setImageURI(it)
        }

        binding.label1.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.takeAphoto.setOnClickListener {
            launchCameraWithPermission()
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
                takePicture.launch()
            } else {
                onError(getString(R.string.permission_denied))
            }
        }

    private fun launchCameraWithPermission() {
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
            activityResultLauncher.launch(permissionArray)
        } else {
            binding.takeAphoto.startAnimation()
            takePicture.launch()
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.imageUploadedLiveData) {
            binding.takeAphoto.revertAnimation()
            withDelay(1000) {
                findNavController().navigate(
                    StoveSetupPhotoFragmentDirections.actionStoveSetupPhotoFragmentToStoveSetupBurnersFragment(
                        StoveSetupBurnersArgs(brand = args.params.brand, type = args.params.type)
                    )
                )
            }
        }
    }
}

@Parcelize
data class StoveSetupPhotoArgs(val brand: String, val type: String) : Parcelable
