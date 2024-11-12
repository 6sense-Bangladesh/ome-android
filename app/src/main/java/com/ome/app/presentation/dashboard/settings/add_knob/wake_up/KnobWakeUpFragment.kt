package com.ome.app.presentation.dashboard.settings.add_knob.wake_up

import android.os.Parcelable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentKnobWakeUpBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.metal_plate.MetalPlateInstallationParams
import com.ome.app.utils.Constants
import com.ome.app.utils.enableCustomTabClick
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class KnobWakeUpFragment : BaseFragment<KnobWakeUpViewModel, FragmentKnobWakeUpBinding>(FragmentKnobWakeUpBinding::inflate) {

    override val viewModel: KnobWakeUpViewModel by viewModels()

    private val args by navArgs<KnobWakeUpFragmentArgs>()

    override fun setupUI() {
        binding.labelInstruction.enableCustomTabClick(
            url = Constants.INSTRUCTION_URL,
            urlText = "installation instructions"
        )
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setOnClickListener {
            navigateSafe(
                KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToMetalPlateInstallationFragment(
                    MetalPlateInstallationParams(selectedKnobPosition = args.params.selectedKnobPosition)
                )
            )
        }
    }

}

@Parcelize
data class KnobWakeUpParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
