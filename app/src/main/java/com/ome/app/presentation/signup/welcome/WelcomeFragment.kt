package com.ome.app.presentation.signup.welcome

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentWelcomeBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.stove.StoveSetupBrandArgs
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressedIgnoreCallback
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class WelcomeFragment :
    BaseFragment<WelcomeViewModel, FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    override val viewModel: WelcomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logo.applyInsetter {
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
        if(BuildConfig.DEBUG){
            binding.textView.setBounceClickListener {
                navigateSafe(R.id.stoveSetupCompletedFragment)
            }
        }
        binding.continueBtn.setBounceClickListener {
            navigateSafe(WelcomeFragmentDirections.actionWelcomeFragmentToStoveSetupBrandFragment(StoveSetupBrandArgs()))
//            binding.continueBtn.startAnimation()
            viewModel.setup()
        }

    }


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
        }
        subscribe(viewModel.fetchUserDataStatus) {
//            navigateSafe(R.id.action_welcomeFragment_to_stoveSetupBrandFragment)
        }
    }

    override fun handleBackPressEvent() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            mainViewModel.selectedBurnerIndex = null
            onBackPressedIgnoreCallback()
        }
    }
}
