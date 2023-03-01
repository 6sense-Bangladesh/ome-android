package com.ome.app.ui.dashboard.settings.add_knob.installation

import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentKnobInstallationManual2Binding
import com.ome.app.base.BaseFragment
import com.ome.app.base.EmptyViewModel
import com.ome.app.ui.dashboard.settings.add_knob.zone.ZoneSelectionFragmentParams
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class KnobInstallationManual2Fragment :
    BaseFragment<EmptyViewModel, FragmentKnobInstallationManual2Binding>(
        FragmentKnobInstallationManual2Binding::inflate
    ) {
    override val viewModel: EmptyViewModel by viewModels()


    private val args by navArgs<KnobInstallationManual2FragmentArgs>()

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
        binding.labelTv.movementMethod = LinkMovementMethod.getInstance()
        binding.labelTv.setLinkTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue_color
            )
        )
        mainViewModel.connectToSocket()

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                KnobInstallationManual2FragmentDirections.actionKnobInstallationManual2FragmentToZoneSelectionFragment(
                    ZoneSelectionFragmentParams(isComeFromSettings = args.params.isComeFromSettings, macAddr = args.params.macAddr)
                )
            )
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
    }
}

@Parcelize
data class KnobInstallationManual2FragmentParams(
    val isComeFromSettings: Boolean = true,
    val macAddr: String = ""
) : Parcelable
