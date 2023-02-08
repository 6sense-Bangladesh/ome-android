package com.ome.app.ui.dashboard.settings.add_knob.burner

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentSelectBurnerBinding
import com.ome.app.base.BaseFragment
import com.ome.app.ui.dashboard.settings.add_knob.scanner.QrCodeScannerParams
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class SelectBurnerFragment : BaseFragment<SelectBurnerViewModel, FragmentSelectBurnerBinding>(
    FragmentSelectBurnerBinding::inflate
) {
    override val viewModel: SelectBurnerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }

        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.burnerSelectionView.onBurnerSelect = { index ->
            binding.continueBtn.isEnabled = true
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ome_gradient_button_unpressed_color
            )
                ?.let {
                    binding.continueBtn.drawableBackground = it
                }
            binding.continueBtn.setBackgroundResource(R.drawable.ome_gradient_button_unpressed_color)
            viewModel.selectedBurnerIndex = index
        }
        binding.continueBtn.setOnClickListener {
            viewModel.selectedBurnerIndex?.let {
                findNavController().navigate(SelectBurnerFragmentDirections.actionSelectBurnerFragmentToQrCodeScannerFragment(
                    QrCodeScannerParams(selectedIndex = it)
                ))
            }
        }

        viewModel.loadData()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.selectedIndexesLiveData) {
            binding.burnerSelectionView.initStoveBurners(it.first, it.second)
        }

    }
}
