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
import com.ome.app.utils.isNotNull
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
            when(mainViewModel.userInfo.value.stoveType?.apply {
                viewModel.stoveType = this
            }){
                StoveType.GAS_TOP -> binding.gasTop.isChecked = true
                StoveType.ELECTRIC_TOP -> binding.electricTop.isChecked = true
                StoveType.GAS_RANGE -> binding.gasRange.isChecked = true
                StoveType.ELECTRIC_RANGE -> binding.electricRange.isChecked = true
                null -> Unit
            }
        }else{
            when(mainViewModel.stoveData.stoveType?.apply {
                viewModel.stoveType = this
            }){
                StoveType.GAS_TOP -> binding.gasTop.isChecked = true
                StoveType.ELECTRIC_TOP -> binding.electricTop.isChecked = true
                StoveType.GAS_RANGE -> binding.gasRange.isChecked = true
                StoveType.ELECTRIC_RANGE -> binding.electricRange.isChecked = true
                null -> Unit
            }
        }
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            mainViewModel.stoveData.stoveGasOrElectric = viewModel.stoveType?.type
            mainViewModel.stoveData.stoveKnobMounting = viewModel.stoveType?.mounting

            if(mainViewModel.stoveData.stoveType == null)
                onError("Please select stove type")
            else if (isFromDeepLink) {
                binding.continueBtn.startAnimation()
                viewModel.saveStoveType(mainViewModel.userInfo.value.stoveId, onEnd = mainViewModel::getUserInfo)
            }
            else{
                mainViewModel.stoveData.stoveType.isNotNull {
                    findNavController().navigate(
                        R.id.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment, bundleOf(
                            "params" to StoveSetupPhotoArgs(
                                brand = args?.brand.orEmpty(),
                                type = it,
                            )
                        )
                    )
                } ?: onError("Please select stove type")

            }
//                findNavController().navigate(
//                    StoveSetupTypeFragmentDirections.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment(
//                        StoveSetupPhotoArgs(args.params.brand, viewModel.stoveType)
//                    )
//                )

        }
        binding.stoveShipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when(checkedIds.first()){
                R.id.gasTop -> viewModel.stoveType = StoveType.GAS_TOP
                R.id.electricTop -> viewModel.stoveType = StoveType.ELECTRIC_TOP
                R.id.gasRange -> viewModel.stoveType = StoveType.GAS_RANGE
                R.id.electricRange -> viewModel.stoveType = StoveType.ELECTRIC_RANGE
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
