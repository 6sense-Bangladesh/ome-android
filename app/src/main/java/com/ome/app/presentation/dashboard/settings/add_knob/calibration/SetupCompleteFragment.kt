package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.content.Context
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.databinding.FragmentSetupCompleteBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*

class SetupCompleteFragment :
    BaseFragment<SetupCompleteViewModel, FragmentSetupCompleteBinding>(
        FragmentSetupCompleteBinding::inflate
    ) {

    private val args by navArgs<SetupCompleteFragmentArgs>()

    override val viewModel: SetupCompleteViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        mainViewModel.getAllKnobsUntilNotEmpty()
        if(args.isComeFromSettings){
            binding.setupAnotherKnobBtn.makeGone()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel.selectedDirection = null
        mainViewModel.selectedDualZone = null
    }

    override fun setupListener() {

        binding.setupAnotherKnobBtn.setBounceClickListener {
            mainViewModel.selectedBurnerIndex = null
            popBackSafe(R.id.knobWakeUpFragment, false)
        }

        binding.imDoneBtn.setBounceClickListener {
            mainViewModel.selectedBurnerIndex = null
            if(args.isComeFromSettings)
                popBackSafe(R.id.deviceSettingsFragment, false) ?: popBackSafe(R.id.deviceDetailsFragment, false)
            else
                popBackSafe(R.id.dashboardFragment, false) ?: navigateSafe(R.id.actionDashboardFragment)
        }

    }

    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(this){}
    }
}
