package com.ome.app.ui.stove

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.databinding.FragmentStoveSetupBrandBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.navigation.DeepNavGraph
import com.ome.app.ui.base.navigation.DeepNavGraph.encode
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoveSetupBrandFragment : BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(FragmentStoveSetupBrandBinding::inflate) {
    override val viewModel: StoveSetupBrandViewModel by viewModels()

    override fun onResume() {
        super.onResume()
//        viewModel.selectedBrand = viewModel.brandArray.find{
//            it == binding.stoveSelector.text.toString()
//        }.orEmpty()
        mainViewModel.stoveData.stoveMakeModel?.let {
            binding.stoveSelector.setText(it)
            viewModel.selectedBrand = it
        }
        binding.stoveSelector.setSimpleItems(viewModel.brandArray.toTypedArray())
        if(isFromDeepLink)
            binding.continueBtn.text =  getString(R.string.update)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)
//        binding.stoveSelector.setSimpleItems(viewModel.brandArray.toTypedArray())
//        binding.stoveSelector.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.brandArray))

        binding.stoveSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedBrand = viewModel.brandArray.getOrNull(position).orEmpty()
                viewModel.selectedBrand = selectedBrand
            }
//        binding.imageView2.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        binding.appBarLayout.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            mainViewModel.stoveData.stoveMakeModel = viewModel.selectedBrand
            if(mainViewModel.stoveData.stoveMakeModel.isNullOrEmpty())
                onError("Please select a brand")
            else if(isFromDeepLink){
                binding.continueBtn.startAnimation()
                viewModel.updateSelectedBrand(mainViewModel.userInfo.value.stoveId, ::onBackPressed)
            }
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

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
