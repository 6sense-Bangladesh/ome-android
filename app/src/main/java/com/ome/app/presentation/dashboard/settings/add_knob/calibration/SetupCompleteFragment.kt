package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentSetupCompleteBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.makeGone

class SetupCompleteFragment :
    BaseFragment<SetupCompleteViewModel, FragmentSetupCompleteBinding>(
        FragmentSetupCompleteBinding::inflate
    ) {

    private val args by navArgs<SetupCompleteFragmentArgs>()

    override val viewModel: SetupCompleteViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        if(args.isComeFromSettings){
            binding.setupAnotherKnobBtn.makeGone()
        }
    }

    override fun setupListener() {

        binding.setupAnotherKnobBtn.setOnClickListener {
            mainViewModel.selectedBurnerIndex = null
            findNavController().popBackStack(R.id.knobWakeUpFragment, false)
        }

        binding.imDoneBtn.setOnClickListener {
            mainViewModel.selectedBurnerIndex = null
            if(args.isComeFromSettings)
                findNavController().popBackStack(R.id.deviceDetailsFragment, false)
            else
                findNavController().popBackStack(R.id.dashboardFragment, false)
        }

    }

    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){}
    }
}
