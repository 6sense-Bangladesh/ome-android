package com.ome.app.ui.dashboard.settings.add_knob.wake_up

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentKnobWakeUpBinding
import com.ome.app.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class KnobWakeUpFragment :
    BaseFragment<KnobWakeUpViewModel, FragmentKnobWakeUpBinding>(
        FragmentKnobWakeUpBinding::inflate
    ) {
    override val viewModel: KnobWakeUpViewModel by viewModels()


    private val args by navArgs<KnobWakeUpFragmentArgs>()

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
        binding.label2Tv.movementMethod = LinkMovementMethod.getInstance()
        binding.label2Tv.setLinkTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue_color
            )
        )

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToMetalPlateInstallationFragment(
                    args.isComeFromSettings
                )
            )
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()

    }
}
