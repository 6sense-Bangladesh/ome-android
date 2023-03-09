package com.ome.app.ui.dashboard.settings.add_knob.calibration

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentSetupCompleteBinding
import com.ome.app.base.BaseFragment
import com.ome.app.utils.makeGone
import dev.chrisbanes.insetter.applyInsetter

class SetupCompleteFragment :
    BaseFragment<SetupCompleteViewModel, FragmentSetupCompleteBinding>(
        FragmentSetupCompleteBinding::inflate
    ) {

    private val args by navArgs<SetupCompleteFragmentArgs>()

    override val viewModel: SetupCompleteViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleTv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.imDoneBtn.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }


        if(args.isComeFromSettings){
            binding.setupAnotherKnobBtn.makeGone()
            binding.subLabelTv.makeGone()
            binding.titleTv.makeGone()
        }

        binding.setupAnotherKnobBtn.setOnClickListener {

            findNavController().popBackStack(R.id.knobWakeUpFragment, false)
        }

        binding.imDoneBtn.setOnClickListener {
            if(!args.isComeFromSettings){
                findNavController().popBackStack(R.id.knobWakeUpFragment, true)
            } else {
                findNavController().popBackStack(R.id.knobInstallationManual1Fragment, true)
            }
        }

    }

    override fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }
}
