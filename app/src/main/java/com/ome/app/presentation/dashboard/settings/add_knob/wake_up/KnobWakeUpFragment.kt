package com.ome.app.presentation.dashboard.settings.add_knob.wake_up

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.BuildConfig
import com.ome.app.databinding.FragmentKnobWakeUpBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.metal_plate.MetalPlateInstallationParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class KnobWakeUpFragment : BaseFragment<EmptyViewModel, FragmentKnobWakeUpBinding>(FragmentKnobWakeUpBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    private val args by navArgs<KnobWakeUpFragmentArgs>()
    val params by lazy { args.params }

    override fun setupUI() {
        binding.labelInstruction.enableCustomTabClick(
            url = Constants.INSTRUCTION_URL,
            urlText = "installation instructions"
        )
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            navigateSafe(
                KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToMetalPlateInstallationFragment(
                    MetalPlateInstallationParams(selectedKnobPosition = params.selectedKnobPosition)
                )
            )
        }
        if(BuildConfig.IS_INTERNAL_TESTING){
            binding.shaftIv.setBounceClickListener {
                navigateSafe(KnobWakeUpFragmentDirections.actionKnobWakeUpFragmentToSetupCompleteFragment(false))
            }
        }
    }

}

@Keep
@Parcelize
data class KnobWakeUpParams(
    val selectedKnobPosition: Int = -1
) : Parcelable
