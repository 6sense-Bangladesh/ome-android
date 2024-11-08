package com.ome.app.presentation.stove

import android.os.Parcelable
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBrandBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupBrandFragment : BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(FragmentStoveSetupBrandBinding::inflate) {
    override val viewModel: StoveSetupBrandViewModel by viewModels()

    private val args by navArgs<StoveSetupBrandFragmentArgs>()

    override fun setupUI() {
        if(args.params.isEditMode) {
            binding.continueBtn.text = getString(R.string.save)
            mainViewModel.userInfo.value.stoveMakeModel
        }else{
            mainViewModel.stoveData.stoveMakeModel
        }?.also {
            binding.stoveSelector.setText(it)
            viewModel.selectedBrand = it
        }
        binding.stoveSelector.setSimpleItems(viewModel.brandArray.toTypedArray())
    }

    override fun setupListener() {
        binding.stoveSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedBrand = viewModel.brandArray.getOrNull(position).orEmpty()
                viewModel.selectedBrand = selectedBrand
            }

        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            mainViewModel.stoveData.stoveMakeModel = viewModel.selectedBrand
            if(mainViewModel.stoveData.stoveMakeModel.isNullOrEmpty())
                onError("Please select a brand")
            else if(args.params.isEditMode)
                viewModel.updateSelectedBrand(mainViewModel.userInfo.value.stoveId, onEnd= mainViewModel::getUserInfo)
            else{
                navigateSafe(StoveSetupBrandFragmentDirections.actionStoveSetupBrandFragmentToStoveSetupTypeFragment(
                    StoveSetupTypeArgs(brand = viewModel.selectedBrand)
                ))
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle{
            if(it)
                binding.continueBtn.startAnimation()
            else {
                binding.continueBtn.revertAnimation()
                if (args.params.isEditMode) {
                    toast(getString(R.string.stove_brand_changed))
                    onBackPressed()
                }
            }
        }
    }
}

@Parcelize
data class StoveSetupBrandArgs(
    val isEditMode: Boolean = false
) : Parcelable
