package com.ome.app.ui.stove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.databinding.FragmentStoveSetupBrandBinding
import com.ome.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class StoveSetupBrandFragment :
    BaseFragment<StoveSetupBrandViewModel, FragmentStoveSetupBrandBinding>(
        FragmentStoveSetupBrandBinding::inflate
    ) {

    override val viewModel: StoveSetupBrandViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarTheme(true)
        binding.stoveSelector.setItems(viewModel.brandArray)
        viewModel.selectedBrand = viewModel.brandArray[binding.stoveSelector.selectedIndex]
        binding.stoveSelector.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedBrand = viewModel.brandArray[position]
        }
        binding.imageView2.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                StoveSetupBrandFragmentDirections.actionStoveSetupBrandFragmentToStoveSetupTypeFragment(
                    StoveSetupTypeArgs(brand = viewModel.selectedBrand)
                )
            )
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}
