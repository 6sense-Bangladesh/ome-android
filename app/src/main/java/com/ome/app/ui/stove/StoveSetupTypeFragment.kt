package com.ome.app.ui.stove

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupTypeBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupTypeFragment :
    BaseFragment<StoveSetupTypeViewModel, FragmentStoveSetupTypeBinding>(
        FragmentStoveSetupTypeBinding::inflate
    ) {

    override val viewModel: StoveSetupTypeViewModel by viewModels()

//    private val args by navArgs<StoveSetupTypeFragmentArgs>()
private val args by lazy { Screens.StoveType.getData(arguments) }

    override fun onResume() {
        super.onResume()
        if(isFromDeepLink)
            binding.continueBtn.text =  getString(R.string.update)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

//        initButtonsStates()
//        binding.imageView2.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        if (isFromDeepLink) {
            binding.continueBtn.text = getString(R.string.save)
            mainViewModel.userInfo.value.let {
                when(it.stoveGasOrElectric){
                    "gas" -> {
                        binding.gasStove.isChecked = true
                        viewModel.stoveType = "gas"
                    }
                    "electric" -> {
                        binding.electricStove.isChecked = true
                        viewModel.stoveType = "electric"
                    }
                    "gasRange" -> {
                        binding.gasRange.isChecked = true
                        viewModel.stoveType = "gas"
                    }
                    "electricRange" -> {
                        binding.electricRange.isChecked = true
                        viewModel.stoveType = "electric"
                    }
                }
            }
        }else{
            when(mainViewModel.stoveData.stoveGasOrElectric){
                "gas" -> {
                    binding.gasStove.isChecked = true
                    viewModel.stoveType = "gas"
                }
                "electric" -> {
                    binding.electricStove.isChecked = true
                    viewModel.stoveType = "electric"
                }
                "gasRange" -> {
                    binding.gasRange.isChecked = true
                    viewModel.stoveType = "gas"
                }
                "electricRange" -> {
                    binding.electricRange.isChecked = true
                    viewModel.stoveType = "electric"
                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            if(mainViewModel.stoveData.stoveGasOrElectric.isNullOrEmpty())
                onError("Please select stove type")
            else if (isFromDeepLink) {
                binding.continueBtn.startAnimation()
                viewModel.saveStoveType(mainViewModel.userInfo.value.stoveId, onEnd = mainViewModel::getUserInfo)
            }
            else{
                findNavController().navigate(
                    R.id.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment, bundleOf(
                        "params" to StoveSetupPhotoArgs(args?.brand.orEmpty(), mainViewModel.stoveData.stoveGasOrElectric.orEmpty())
                    )
                )
            }
//                findNavController().navigate(
//                    StoveSetupTypeFragmentDirections.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment(
//                        StoveSetupPhotoArgs(args.params.brand, viewModel.stoveType)
//                    )
//                )

        }
        binding.stoveShipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when(checkedIds.first()){
                R.id.gasStove -> mainViewModel.stoveData.stoveGasOrElectric = "gas"
                R.id.electricStove -> mainViewModel.stoveData.stoveGasOrElectric = "electric"
                R.id.gasRange -> mainViewModel.stoveData.stoveGasOrElectric = "gas"
                R.id.electricRange -> mainViewModel.stoveData.stoveGasOrElectric = "electric"
            }
        }
//        binding.gasIv.setOnClickListener {
//            if (viewModel.stoveType.isEmpty() || viewModel.stoveType == "electric") {
//                binding.continueBtn.isEnabled = true
//                if(args.params.isEditMode){
//                    ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
//                        ?.let {
//                            binding.continueBtn.drawableBackground = it
//                        }
//                    binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
//                } else {
//                    binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
//                }
//                viewModel.stoveType = "gas"
//                changeButtonState(binding.electricIv, false)
//                changeButtonState(binding.gasIv, true)
//
//            }
//        }
//        binding.electricIv.setOnClickListener {
//            if (viewModel.stoveType.isEmpty() || viewModel.stoveType == "gas") {
//                binding.continueBtn.isEnabled = true
//                if(args.params.isEditMode){
//                    ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
//                        ?.let {
//                            binding.continueBtn.drawableBackground = it
//                        }
//                    binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
//                } else {
//                    binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
//                }
//                viewModel.stoveType = "electric"
//                changeButtonState(binding.gasIv, false)
//                changeButtonState(binding.electricIv, true)
//            }
//        }
    }
//
//    private fun initButtonsStates() {
//        if (viewModel.stoveType.isEmpty()) {
//            binding.continueBtn.isEnabled = false
//            changeButtonState(binding.gasIv, false)
//            changeButtonState(binding.electricIv, false)
//        } else {
//            binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
//            when (viewModel.stoveType) {
//                "electric" -> {
//                    changeButtonState(binding.electricIv, true)
//                    changeButtonState(binding.gasIv, false)
//                    binding.continueBtn.isEnabled = true
//                }
//                "gas" -> {
//                    changeButtonState(binding.gasIv, true)
//                    changeButtonState(binding.electricIv, false)
//                    binding.continueBtn.isEnabled = true
//                }
//            }
//        }
//    }

//    private fun changeButtonState(view: ImageView, isPressed: Boolean) {
//        if (isPressed) {
//            view.setBackgroundResource(R.drawable.stove_type_button_shape_pressed)
//            view.setColorFilter(Color.WHITE)
//        } else {
//            view.setBackgroundResource(R.drawable.stove_type_button_shape)
//            view.setColorFilter(
//                ContextCompat.getColor(requireContext(), R.color.gray_color),
//                android.graphics.PorterDuff.Mode.MULTIPLY
//            )
//        }
//    }


    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.loadingLiveData){
            binding.continueBtn.revertAnimation()
            findNavController().popBackStack()
        }
    }
}

@Parcelize
data class StoveSetupTypeArgs(
    val brand: String = "",
    val isEditMode: Boolean = false
) : Parcelable
