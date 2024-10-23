package com.ome.app.ui.stove

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupPhotoBinding
import com.ome.app.ui.base.navigation.DeepNavGraph
import com.ome.app.ui.base.navigation.DeepNavGraph.encode
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupPhotoFragment :
    BasePhotoFragment<StoveSetupPhotoViewModel, FragmentStoveSetupPhotoBinding>(
        FragmentStoveSetupPhotoBinding::inflate
    ) {

    override val viewModel: StoveSetupPhotoViewModel by activityViewModels()

    private val args by navArgs<StoveSetupPhotoFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

//        viewModel.currentContentUri?.let {
//            binding.shaftIv.setImageURI(it)
//        }

//        binding.label1.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
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

    override fun handleTakeAPhotoResult(uri: Uri) {
//        binding.shaftIv.setImageURI(uri)
//        viewModel.uploadImage()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.imageUploadedLiveData) {
            binding.takePhoto.revertAnimation()
            findNavController().navigate(
                R.id.action_stoveSetupPhotoFragment_to_stoveSetupBurnersFragment,
                bundleOf(DeepNavGraph.NAV_ARG to StoveSetupBurnersArgs(brand = args.params.brand, type = args.params.type).encode())
//                StoveSetupPhotoFragmentDirections.actionStoveSetupPhotoFragmentToStoveSetupBurnersFragment(
//                    StoveSetupBurnersArgs(brand = args.params.brand, type = args.params.type), ""
//                )
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
