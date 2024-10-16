package com.ome.app.ui.stove

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentStoveSetupBrandBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoveSetupBrandFragment :
    BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(
        FragmentStoveSetupBrandBinding::inflate
    ) {

    override val viewModel: StoveSetupBrandViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        viewModel.selectedBrand = viewModel.brandArray.find{
            it == binding.stoveSelector.text.toString()
        }.orEmpty()
        binding.stoveSelector.setSimpleItems(viewModel.brandArray.toTypedArray())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)
//        binding.stoveSelector.setSimpleItems(viewModel.brandArray.toTypedArray())
//        binding.stoveSelector.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.brandArray))

        binding.stoveSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.selectedBrand = viewModel.brandArray.getOrNull(position).orEmpty()
            }
//        binding.imageView2.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
        binding.appBarLayout.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            if(viewModel.selectedBrand.isNotEmpty()) {
                findNavController().navigate(
                    StoveSetupBrandFragmentDirections.actionStoveSetupBrandFragmentToStoveSetupTypeFragment(
                        StoveSetupTypeArgs(brand = viewModel.selectedBrand)
                    )
                )
            }
            else
                toast("Please select a brand")
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
