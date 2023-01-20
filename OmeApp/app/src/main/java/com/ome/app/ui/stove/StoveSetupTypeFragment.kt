package com.ome.app.ui.stove

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentStoveSetupTypeBinding
import com.ome.app.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class StoveSetupTypeFragment :
    BaseFragment<StoveSetupTypeViewModel, FragmentStoveSetupTypeBinding>(
        FragmentStoveSetupTypeBinding::inflate
    ) {

    override val viewModel: StoveSetupTypeViewModel by viewModels()

    private val args by navArgs<StoveSetupTypeFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        initButtonsStates()
        binding.imageView2.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                StoveSetupTypeFragmentDirections.actionStoveSetupTypeFragmentToStoveSetupPhotoFragment(
                    StoveSetupPhotoArgs(args.brand, viewModel.stoveType)
                )
            )
        }
        binding.gasIv.setOnClickListener {
            if (viewModel.stoveType.isEmpty() || viewModel.stoveType == "electric") {
                binding.continueBtn.isEnabled = true
                binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
                viewModel.stoveType = "gas"
                changeButtonState(binding.electricIv, false)
                changeButtonState(binding.gasIv, true)

            }
        }
        binding.electricIv.setOnClickListener {
            if (viewModel.stoveType.isEmpty() || viewModel.stoveType == "gas") {
                binding.continueBtn.isEnabled = true
                binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
                viewModel.stoveType = "electric"
                changeButtonState(binding.gasIv, false)
                changeButtonState(binding.electricIv, true)
            }
        }
    }

    private fun initButtonsStates() {
        if (viewModel.stoveType.isEmpty()) {
            binding.continueBtn.isEnabled = false
            changeButtonState(binding.gasIv, false)
            changeButtonState(binding.electricIv, false)
        } else {
            binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_selector)
            when (viewModel.stoveType) {
                "electric" -> {
                    changeButtonState(binding.electricIv, true)
                    changeButtonState(binding.gasIv, false)
                    binding.continueBtn.isEnabled = true
                }
                "gas" -> {
                    changeButtonState(binding.gasIv, true)
                    changeButtonState(binding.electricIv, false)
                    binding.continueBtn.isEnabled = true
                }
            }
        }
    }

    private fun changeButtonState(view: ImageView, isPressed: Boolean) {
        if (isPressed) {
            view.setBackgroundResource(R.drawable.stove_type_button_shape_pressed)
            view.setColorFilter(Color.WHITE)
        } else {
            view.setBackgroundResource(R.drawable.stove_type_button_shape)
            view.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.gray_color),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
    }


    override fun observeLiveData() {
        super.observeLiveData()
    }
}
