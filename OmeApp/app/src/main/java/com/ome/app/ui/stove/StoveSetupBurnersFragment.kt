package com.ome.app.ui.stove

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentStoveSetupBurnersBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.subscribe
import com.ome.app.utils.withDelay
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize


@AndroidEntryPoint
class StoveSetupBurnersFragment :
    BaseFragment<StoveSetupBurnersViewModel, FragmentStoveSetupBurnersBinding>(
        FragmentStoveSetupBurnersBinding::inflate
    ) {

    override val viewModel: StoveSetupBurnersViewModel by viewModels()

    private val args by navArgs<StoveSetupBurnersFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        viewModel.brand = args.params.brand
        viewModel.type = args.params.type

        binding.titleTv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.fourBurnersIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 4
        }
        binding.fourBarBurnersIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 51
        }
        binding.fiveBurnersIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 5
        }
        binding.sixBurnersIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 6
        }
        binding.twoBurnersHorizontalIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 2
        }
        binding.twoBurnersVerticalIv.setOnClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = 21
        }


        binding.continueBtn.setOnClickListener {
            binding.continueBtn.startAnimation()
            viewModel.createStove()
        }
    }


    private fun handleButtonClick(view: View) {
        resetAllButtons()
        view.isSelected = true
        binding.continueBtn.isEnabled = true
        ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
            ?.let {
                binding.continueBtn.drawableBackground = it
            }
        binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
    }


    private fun resetAllButtons() {
        binding.fourBurnersIv.isSelected = false
        binding.fourBarBurnersIv.isSelected = false
        binding.fiveBurnersIv.isSelected = false
        binding.sixBurnersIv.isSelected = false
        binding.twoBurnersHorizontalIv.isSelected = false
        binding.twoBurnersVerticalIv.isSelected = false
    }


    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.createStoveLiveData) {
            binding.continueBtn.revertAnimation()
            withDelay(1000){
                findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
            }

        }
    }
}

@Parcelize
data class StoveSetupBurnersArgs(val brand: String, val type: String) : Parcelable
