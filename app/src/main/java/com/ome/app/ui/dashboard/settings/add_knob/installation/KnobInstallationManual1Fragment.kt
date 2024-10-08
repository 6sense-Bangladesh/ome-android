package com.ome.app.ui.dashboard.settings.add_knob.installation

import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentKnobInstallationManual1Binding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.ui.base.EmptyViewModel
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.parcelize.Parcelize

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

        if (!args.params.isComeFromSettings) {
            binding.backIv.makeGone()
        } else {
            binding.backIv.makeVisible()
        }

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
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
                    KnobInstallationManual2FragmentParams(
                        macAddr = args.params.macAddr,
                        isComeFromSettings = args.params.isComeFromSettings
                    )
                )
            )

        }
    }

    override fun handleBackPressEvent() {
        binding.backIv.makeGone()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.params.isComeFromSettings) {
                    findNavController().popBackStack()
                }
            }
        })
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}

@Parcelize
data class KnobInstallationManual1FragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddr: String = ""
) : Parcelable
