package com.ome.app.ui.stove

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBurnersBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupBurnersFragment :
    BaseFragment<StoveSetupBurnersViewModel, FragmentStoveSetupBurnersBinding>(
        FragmentStoveSetupBurnersBinding::inflate
    ) {

    override val viewModel: StoveSetupBurnersViewModel by viewModels()

//    private val args by navArgs<StoveSetupBurnersFragmentArgs>()
    private val args by lazy { Screens.StoveLayout.getData(arguments) }

    override fun onResume() {
        super.onResume()
        if(isFromDeepLink)
            binding.continueBtn.text =  getString(R.string.update)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)

        args?.let {
            viewModel.brand = it.brand
            viewModel.type = it.type

        }
//        binding.titleTv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }

        binding.fourBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.FOUR_BURNERS
        }
        binding.fourBarBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.FOUR_BAR_BURNERS
        }
        binding.fiveBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.FIVE_BURNERS
        }
        binding.sixBurnersIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.SIX_BURNERS
        }
        binding.twoBurnersHorizontalIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.TWO_BURNERS_HORIZONTAL
        }
        binding.twoBurnersVerticalIv.setBounceClickListener {
            handleButtonClick(it)
            viewModel.stoveOrientation = StoveOrientation.TWO_BURNERS_VERTICAL
        }

        if(isFromDeepLink){
            binding.continueBtn.text = getString(R.string.update)
        }else{
            mainViewModel.stoveData.stoveOrientation.enum?.let {
                viewModel.stoveOrientation = it
                activity?.findViewById<AppCompatImageView>(it.layoutRes)?.let { burnerIv ->
                    handleButtonClick(burnerIv)
                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        if(BuildConfig.DEBUG){
            binding.titleTv.setBounceClickListener {
                findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
            }
        }
        binding.continueBtn.setBounceClickListener  {
            if(isFromDeepLink){
                binding.continueBtn.startAnimation()
                mainViewModel.stoveData.stoveOrientation = viewModel.stoveOrientation?.number
                viewModel.updateStoveOrientation(mainViewModel.userInfo.value.stoveId, onEnd = mainViewModel::getUserInfo)
            } else {
                mainViewModel.stoveData.stoveOrientation = viewModel.stoveOrientation?.number
                viewModel.updateUserStove(stoveId= mainViewModel.userInfo.value.stoveId)
                binding.continueBtn.startAnimation()
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
            mainViewModel.getUserInfo()
            binding.continueBtn.revertAnimation()
            findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
        }

        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
            if(isFromDeepLink)
                findNavController().popBackStack()
        }
    }
}

@Parcelize
data class StoveSetupBurnersArgs(
    val brand: String = "",
    val type: String = "",
    val isEditMode: Boolean = false
) : Parcelable
