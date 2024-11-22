package com.ome.app.presentation.stove

import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBurnersBinding
import com.ome.app.domain.model.state.StoveOrientation
import com.ome.app.domain.model.state.StoveType
import com.ome.app.domain.model.state.stoveOrientation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupBurnersFragment :
    BaseFragment<StoveSetupBurnersViewModel, FragmentStoveSetupBurnersBinding>(
        FragmentStoveSetupBurnersBinding::inflate
    ) {

    override val viewModel: StoveSetupBurnersViewModel by viewModels()

    private val args by navArgs<StoveSetupBurnersFragmentArgs>()

    override fun setupUI() {
        args.let {
            viewModel.brand = it.params.brand
            it.params.type?.apply {
                viewModel.stoveType = type
                viewModel.stoveKnobMounting = mounting
            }
        }
        if (args.params.isEditMode) {
            mainViewModel.knobs.value.isNotEmpty {
                binding.textWarn.visible()
                binding.continueBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
            }
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
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
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
        if(BuildConfig.DEBUG){
            binding.titleTv.setBounceClickListener {
                navigateSafe(StoveSetupBurnersFragmentDirections.actionStoveSetupBurnersFragmentToStoveSetupCompletedFragment())
            }
        }
        binding.continueBtn.setBounceClickListener  {
            if(mainViewModel.knobs.value.isNotEmpty() && args.params.isEditMode) return@setBounceClickListener
            if (args.params.isEditMode) {
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
            navigateSafe(R.id.action_stoveSetupBurnersFragment_to_stoveSetupCompletedFragment)
        }

        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if (args.params.isEditMode) {
                    toast(getString(R.string.stove_layout_changed))
                    onBackPressed()
                }
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
