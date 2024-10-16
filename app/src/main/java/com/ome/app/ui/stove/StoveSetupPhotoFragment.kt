package com.ome.app.ui.stove

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentStoveSetupPhotoBinding
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupPhotoFragment :
    BasePhotoFragment<StoveSetupPhotoViewModel, FragmentStoveSetupPhotoBinding>(
        FragmentStoveSetupPhotoBinding::inflate
    ) {

    override val viewModel: StoveSetupPhotoViewModel by viewModels()

    private val args by navArgs<StoveSetupPhotoFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        viewModel.currentContentUri?.let {
            binding.shaftIv.setImageURI(it)
        }

//        binding.label1.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        binding.appBarLayout.setNavigationOnClickListener(::onBackPressed)
        binding.takeAphoto.setBounceClickListener{
            launchCameraWithPermission()
        }
    }

    override fun handleTakeAPhotoResult(uri: Uri) {
        binding.shaftIv.setImageURI(uri)
        viewModel.uploadImage()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.imageUploadedLiveData) {
            binding.takeAphoto.revertAnimation()
            findNavController().navigate(
                StoveSetupPhotoFragmentDirections.actionStoveSetupPhotoFragmentToStoveSetupBurnersFragment(
                    StoveSetupBurnersArgs(brand = args.params.brand, type = args.params.type)
                )
            )
        }
        subscribe(viewModel.loadingLiveData) {
            if(it){
                binding.takeAphoto.startAnimation()
            } else {
                binding.takeAphoto.revertAnimation()
            }
        }
    }
}

@Parcelize
data class StoveSetupPhotoArgs(val brand: String, val type: String) : Parcelable
