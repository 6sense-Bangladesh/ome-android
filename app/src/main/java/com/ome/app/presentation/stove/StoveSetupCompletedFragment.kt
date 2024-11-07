package com.ome.app.presentation.stove

import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentStoveSetupCompletedBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.settings.add_knob.wake_up.KnobWakeUpParams
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoveSetupCompletedFragment :
    BaseFragment<StoveSetupCompletedViewModel, FragmentStoveSetupCompletedBinding>(
        FragmentStoveSetupCompletedBinding::inflate
    ) {
    override val viewModel: StoveSetupCompletedViewModel by viewModels()

    override fun setupListener() {
        binding.finishBtn.setBounceClickListener {
            findNavController().navigate(StoveSetupCompletedFragmentDirections.actionStoveSetupCompletedFragmentToKnobWakeUpFragment(KnobWakeUpParams()))
        }
        binding.skipKnobSetupBtn.setBounceClickListener {
//            Screens.Dashboard.navigate(data = Unit,popUpToInclusive = true)
            findNavController().navigate(StoveSetupCompletedFragmentDirections.actionStoveSetupCompletedFragmentToDashboardFragment())
        }
    }

    override fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(this){}
    }
}
