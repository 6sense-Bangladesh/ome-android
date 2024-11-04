package com.ome.app.presentation.stove

import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBurnersBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.navigation.DeepNavGraph.getData
import com.ome.app.presentation.base.navigation.Screens
import com.ome.app.utils.collectWithLifecycle
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

    override fun setupUI() {
        args.let {
            viewModel.brand = it.brand
            it.type?.apply {
                viewModel.stoveType = type
                viewModel.stoveKnobMounting = mounting
            }
        }
        if (args.isEditMode) {
            binding.continueBtn.text = getString(R.string.save)
            mainViewModel.userInfo.value.stoveOrientation.stoveOrientation
        }else{
            mainViewModel.stoveData.stoveOrientation.stoveOrientation
        } ?.also {
            viewModel.stoveOrientation = it
            activity?.findViewById<AppCompatImageView>(it.layoutRes)?.let { burnerIv ->
                handleButtonClick(burnerIv)
            }
        }
    }


    override fun setupListener() {
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
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        if(BuildConfig.DEBUG){
            binding.titleTv.setBounceClickListener {
                findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
            }
        }
        binding.continueBtn.setBounceClickListener  {
            if (args.isEditMode) {
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
//        ContextCompat.getDrawable(requireContext(), R.drawable.ome_gradient_button_unpressed_color)
//            ?.let {
//                binding.continueBtn.drawableBackground = it
//            }
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


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.createStoveLiveData) {
            mainViewModel.getUserInfo()
            findNavController().navigate(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
        }

        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if (args.isEditMode)
                    onBackPressed()
            }
        }
    }
}

@Parcelize
data class StoveSetupBurnersArgs(
    val brand: String = "",
    val type: StoveType? = null,
    val isEditMode: Boolean = false
) : Parcelable
