package com.ome.app.presentation.stove

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupPhotoBinding
import com.ome.app.domain.model.state.StoveType
import com.ome.app.presentation.views.camera.ImageViewDialogFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupPhotoFragment :
    BasePhotoFragment<FragmentStoveSetupPhotoBinding>(
        FragmentStoveSetupPhotoBinding::inflate
    ) {

    private val args by navArgs<StoveSetupPhotoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.takePhoto.setBounceClickListener{
            if(binding.takePhoto.text == getString(R.string.take_photo))
                launchCameraWithPermission()
            else if(binding.takePhoto.text == getString(R.string.use_photo))
                viewModel.uploadImage()
        }
        binding.retakePhoto.setBounceClickListener {
            launchCameraWithPermission()
        }
    }


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.imageUploadedLiveData) {
            binding.takePhoto.revertAnimation()
            navigateSafe(
                StoveSetupPhotoFragmentDirections.actionStoveSetupPhotoFragmentToStoveSetupBurnersFragment(
                    StoveSetupBurnersArgs(brand = args.params.brand, type = args.params.type)
                )
            )
        }
        subscribe(viewModel.loadingLiveData) {
            if(it){
                binding.takePhoto.startAnimation()
            } else {
                binding.takePhoto.revertAnimation()
            }
        }
        viewModel.photoList.collectWithLifecycle {
            it.firstOrNull()?.let{ photoFile->
                viewModel.currentFile = photoFile
                binding.shaftIv.loadDrawable(photoFile)
                binding.shaftIv.setBounceClickListener {
                    ImageViewDialogFragment(listOf(photoFile)).show(childFragmentManager, "FullScreenImageDialog")
                }
                binding.retakePhoto.visible()
                binding.takePhoto.text = getString(R.string.use_photo)
//                viewModel.uploadImage(photoFile)
            } ?: run {
                binding.retakePhoto.gone()
                binding.takePhoto.text = getString(R.string.take_photo)
            }
        }
    }
}

@Parcelize
data class StoveSetupPhotoArgs(val brand: String, val type: StoveType) : Parcelable
