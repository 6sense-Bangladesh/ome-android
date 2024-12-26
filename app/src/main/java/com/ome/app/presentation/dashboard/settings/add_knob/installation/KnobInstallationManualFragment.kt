package com.ome.app.presentation.dashboard.settings.add_knob.installation

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentKnobInstallationManualBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.zone.ZoneSelectionFragmentParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class KnobInstallationManualFragment :
    BaseFragment<EmptyViewModel, FragmentKnobInstallationManualBinding>(
        FragmentKnobInstallationManualBinding::inflate
    ) {
    override val viewModel: EmptyViewModel by viewModels()

    private val args by navArgs<KnobInstallationManualFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        binding.shaftIv.loadDrawable(R.drawable.knob_plug)
        mainViewModel.connectToSocket()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG", "onAttach: ${mainViewModel.selectedDualZone} : ${mainViewModel.selectedDirection}")
        mainViewModel.selectedDirection = null
        mainViewModel.selectedDualZone = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.labelTv.movementMethod = LinkMovementMethod.getInstance()
//        binding.labelTv.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.continueBtn.setBounceClickListener {
            navigateSafe(
                KnobInstallationManualFragmentDirections.actionKnobInstallationManualFragmentToZoneSelectionFragment(
                    ZoneSelectionFragmentParams(
                        macAddrs = params.macAddr,
                        isComeFromSettings = params.isComeFromSettings
                    )
                )
            )
        }
    }
    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){
            popBackSafe(if(params.isComeFromSettings) R.id.deviceDetailsFragment else R.id.dashboardFragment, false)
        }
    }
}

@Keep
@Parcelize
data class KnobInstallationManualFragmentParams(
    val isComeFromSettings: Boolean = false,
    val macAddr: String = ""
) : Parcelable
