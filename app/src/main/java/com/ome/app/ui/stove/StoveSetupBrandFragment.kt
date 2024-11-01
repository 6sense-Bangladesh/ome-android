package com.ome.app.ui.stove

import android.os.Parcelable
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBrandBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph
import com.ome.app.ui.base.navigation.DeepNavGraph.encode
import com.ome.app.ui.base.navigation.DeepNavGraph.getData
import com.ome.app.ui.base.navigation.Screens
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


@AndroidEntryPoint
class StoveSetupBrandFragment : BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(FragmentStoveSetupBrandBinding::inflate) {
    override val viewModel: StoveSetupBrandViewModel by viewModels()

    private val args by lazy { Screens.StoveBrand.getData(arguments) }

    override fun setupUI() {
        if(args.isEditMode) {
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
            else if(args.isEditMode)
                viewModel.updateSelectedBrand(mainViewModel.userInfo.value.stoveId, onEnd= mainViewModel::getUserInfo)
            else{
                findNavController().navigate(
                    R.id.action_stoveSetupBrandFragment_to_stoveSetupTypeFragment,
                    bundleOf(DeepNavGraph.NAV_ARG to StoveSetupTypeArgs(brand = viewModel.selectedBrand).encode())
//                    StoveSetupBrandFragmentDirections.actionStoveSetupBrandFragmentToStoveSetupTypeFragment(
//                        StoveSetupTypeArgs(brand = mainViewModel.stoveData.stoveMakeModel.orEmpty())
//                    )
                )
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
                if (args.isEditMode)
                    onBackPressed()
            }
        }
    }
}

@Parcelize
data class StoveSetupBrandArgs(
    val isEditMode: Boolean = false
) : Parcelable
