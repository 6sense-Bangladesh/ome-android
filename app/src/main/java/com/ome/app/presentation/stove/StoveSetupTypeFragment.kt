package com.ome.app.presentation.stove

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupTypeBinding
import com.ome.app.domain.model.state.StoveType
import com.ome.app.domain.model.state.stoveType
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupTypeFragment :
    BaseFragment<StoveSetupTypeViewModel, FragmentStoveSetupTypeBinding>(FragmentStoveSetupTypeBinding::inflate) {

    override val viewModel: StoveSetupTypeViewModel by viewModels()

    private val args by navArgs<StoveSetupTypeFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
//        initButtonsStates()
//        binding.imageView2.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        if (params.isEditMode) {
            binding.continueBtn.text = getString(R.string.save)
            mainViewModel.userInfo.value.stoveType
        }else{
            mainViewModel.stoveData.stoveType
        }.also {
            viewModel.stoveType = it
            it.log("stoveType")
            when(it){
                StoveType.GAS_TOP -> binding.gasTop.isChecked = true
                StoveType.ELECTRIC_TOP -> binding.electricTop.isChecked = true
                StoveType.GAS_RANGE -> binding.gasRange.isChecked = true
                StoveType.ELECTRIC_RANGE -> binding.electricRange.isChecked = true
                null -> Unit
            }
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            mainViewModel.stoveData.stoveGasOrElectric = viewModel.stoveType?.type
            mainViewModel.stoveData.stoveKnobMounting = viewModel.stoveType?.mounting

            if(mainViewModel.stoveData.stoveType == null)
                onError("Please select stove type")
            else if (params.isEditMode) {
                binding.continueBtn.startAnimation()
                viewModel.saveStoveType(mainViewModel.userInfo.value.stoveId, onEnd = mainViewModel::getUserInfo)
            }
            else{
                mainViewModel.stoveData.stoveType.isNotNull {
                    navigateSafe(
                        StoveSetupTypeFragmentDirections.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment(
                            StoveSetupPhotoArgs(params.brand, it)
                        )
                    )
                } ?: onError("Please select stove type")

            }

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
//                if(params.isEditMode){
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
//                if(params.isEditMode){
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
//                ContextCompat.getColor(requireContext(), R.color.gray),
//                android.graphics.PorterDuff.Mode.MULTIPLY
//            )
//        }
//    }


    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if (params.isEditMode) {
                    toast(getString(R.string.stove_type_changed))
                    onBackPressed()
                }
            }
        }
    }
}

@Parcelize
data class StoveSetupTypeArgs(
    val brand: String = "",
    val isEditMode: Boolean = false
) : Parcelable
