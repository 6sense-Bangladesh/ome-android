package com.ome.app.ui.stove

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBurnersBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import com.ome.app.utils.toast
import com.ome.app.utils.withDelay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


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

//        binding.titleTv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }

        binding.fourBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.FOUR_BURNERS.number
        }
        binding.fourBarBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.FOUR_BAR_BURNERS.number
        }
        binding.fiveBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.FIVE_BURNERS.number
        }
        binding.sixBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.SIX_BURNERS.number
        }
        binding.twoBurnersHorizontalIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.TWO_BURNERS_HORIZONTAL.number
        }
        binding.twoBurnersVerticalIv.setBounceClickListener {
            handleButtonClick(it)
            mainViewModel.stoveData.stoveOrientation = StoveOrientation.TWO_BURNERS_VERTICAL.number
        }

        if(args.params.isEditMode){
            binding.continueBtn.text = getString(R.string.save)
        }else{
            mainViewModel.stoveData.stoveOrientation.enum?.let {
                activity?.findViewById<AppCompatImageView>(it.layoutRes)?.let { burnerIv ->
                    handleButtonClick(burnerIv)
                }
            }
        }
        binding.appBarLayout.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener  {
            if(args.params.isEditMode){
                binding.continueBtn.startAnimation()
                viewModel.updateStoveOrientation(args.params.stoveId)
            } else {
                if(mainViewModel.stoveData.stoveOrientation != null) {
                    viewModel.stoveOrientation = mainViewModel.stoveData.stoveOrientation.enum
                    viewModel.createStove()
                    binding.continueBtn.startAnimation()
                }
                else
                    toast("Please select burner type")
            }
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
//        binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
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
            withDelay(1000) {
                findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
            }

        }

        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
            findNavController().popBackStack()
        }
    }
}

@Parcelize
data class StoveSetupBurnersArgs(
    val brand: String = "",
    val stoveId: String = "",
    val type: String = "",
    val isEditMode: Boolean = false
) : Parcelable
