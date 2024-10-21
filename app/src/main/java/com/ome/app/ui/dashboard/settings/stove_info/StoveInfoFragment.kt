package com.ome.app.ui.dashboard.settings.stove_info

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentStoveInfoBinding
import com.ome.app.ui.stove.BasePhotoFragment
import com.ome.app.utils.getPath
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import java.io.File

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@AndroidEntryPoint
class StoveInfoFragment :
    BasePhotoFragment<StoveInfoViewModel, FragmentStoveInfoBinding>(
        FragmentStoveInfoBinding::inflate
    ) {
    override val viewModel: StoveInfoViewModel by viewModels()

//    private val args by navArgs<StoveInfoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.stoveOrientationCl.setOnClickListener {
//            findNavController().navigate(
//                StoveInfoFragmentDirections.actionStoveInfoFragmentToStoveSetupBurnersFragment(
//                    StoveSetupBurnersArgs(isEditMode = true, stoveId = args.params)
//                )
//            )
        }
        binding.stoveTypeCl.setOnClickListener {
//            findNavController().navigate(
//                StoveInfoFragmentDirections.actionStoveInfoFragmentToStoveSetupTypeFragment(
//                    StoveSetupTypeArgs(isEditMode = true, stoveId = args.params)
//                )
//            )
        }

        binding.stoveImageIv.setOnClickListener {
            showPhotoSelectionDialog()
        }

        viewModel.loadData()
//        viewModel.stoveId = args.params
    }


    private fun showPhotoSelectionDialog() {
        val dialog =
            PhotoSelectionTypeDialogFragment()
        dialog.onChooseFromPhoneClick = {
            openPhoneGallery()
        }
        dialog.onTakeAPhotoClick = {
            launchCameraWithPermission()
        }
        dialog.show(
            parentFragmentManager,
            "photo_selection_dialog"
        )
    }

    override fun handleTakeAPhotoResult(uri: Uri) {
        binding.stoveImageIv.setImageURI(uri)
        viewModel.uploadImage()
    }

    override fun handleChooseFromPhoneResult(uri: Uri) {
        val path = getPath(requireContext(), uri)
        path?.let { it ->
            binding.stoveImageIv.setImageURI(uri)
            viewModel.currentContentUri = uri
            viewModel.currentFile = File(it)
            viewModel.uploadImage()
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.stoveNameLiveData) { type ->
            binding.firstNameEt.setText(type)
        }
        subscribe(viewModel.stoveTypeLiveData) { type ->
            binding.stoveTypeIv.setImageResource(type.imgRes)
        }
        subscribe(viewModel.stoveOrientationLiveData) { orientation ->
            binding.stoveOrientationIv.setImageResource(orientation.imgRes)
        }

    }
}
