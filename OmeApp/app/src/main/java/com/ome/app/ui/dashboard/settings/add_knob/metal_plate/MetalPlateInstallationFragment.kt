package com.ome.app.ui.dashboard.settings.add_knob.metal_plate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.databinding.FragmentMetalPlateBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class MetalPlateInstallationFragment :
    BaseFragment<MetalPlateInstallationViewModel, FragmentMetalPlateBinding>(
        FragmentMetalPlateBinding::inflate
    ) {
    override val viewModel: MetalPlateInstallationViewModel by viewModels()

    private val args by navArgs<MetalPlateInstallationFragmentArgs>()

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
        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                MetalPlateInstallationFragmentDirections.actionMetalPlateInstallationFragmentToSelectBurnerFragment(
                    SelectBurnerFragmentParams(
                        isComeFromSettings = args.isComeFromSettings,
                        isChangeMode = false
                    )
                )
            )
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

    }

    override fun observeLiveData() {
        super.observeLiveData()

    }
}
