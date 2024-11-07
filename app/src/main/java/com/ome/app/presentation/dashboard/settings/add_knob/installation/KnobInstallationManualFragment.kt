package com.ome.app.presentation.dashboard.settings.add_knob.installation

import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentKnobInstallationManualBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.zone.ZoneSelectionFragmentParams
import com.ome.app.utils.loadDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class KnobInstallationManualFragment :
    BaseFragment<EmptyViewModel, FragmentKnobInstallationManualBinding>(
        FragmentKnobInstallationManualBinding::inflate
    ) {
    override val viewModel: EmptyViewModel by viewModels()

    private val args by navArgs<KnobInstallationManualFragmentArgs>()

    override fun setupUI() {
        binding.shaftIv.loadDrawable(R.drawable.knob_plug)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.labelTv.movementMethod = LinkMovementMethod.getInstance()
        binding.labelTv.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener{
            findNavController().popBackStack(R.id.dashboardFragment, false)
        }

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(
                KnobInstallationManualFragmentDirections.actionKnobInstallationManualFragmentToZoneSelectionFragment(
                    ZoneSelectionFragmentParams(
                        macAddrs = args.params.macAddr,
                        isComeFromSettings = args.params.isComeFromSettings
                    )
                )
            )
        }
    }
    override fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().popBackStack(R.id.dashboardFragment, false)
        }
    }
}

@Parcelize
data class KnobInstallationManualFragmentParams(
    val isComeFromSettings: Boolean = false,
    val macAddr: String = ""
) : Parcelable
