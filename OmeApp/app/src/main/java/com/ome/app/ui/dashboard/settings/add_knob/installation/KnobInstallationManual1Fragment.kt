package com.ome.app.ui.dashboard.settings.add_knob.installation

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentKnobInstallationManual1Binding
import com.ome.app.base.BaseFragment
import com.ome.app.base.EmptyViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class KnobInstallationManual1Fragment :
    BaseFragment<EmptyViewModel, FragmentKnobInstallationManual1Binding>(
        FragmentKnobInstallationManual1Binding::inflate
    ) {
    override val viewModel: EmptyViewModel by viewModels()


    private val args by navArgs<KnobInstallationManual1FragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTv.applyInsetter {
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
        binding.labelTv.movementMethod = LinkMovementMethod.getInstance()
        binding.labelTv.setLinkTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue_color
            )
        )

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                KnobInstallationManual1FragmentDirections.actionKnobInstallationManual1FragmentToKnobInstallationManual2Fragment(
                    args.macAddr
                )
            )

        }
    }
    override fun observeLiveData() {
        super.observeLiveData()
    }
}
