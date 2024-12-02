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

    private lateinit var userName: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = mainViewModel.userInfo.value.firstName.toString()
        binding.congoUser.text = getString(R.string.congoUser, userName)

        binding.startSetup.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                margin(bottom = true)
            }
        }
        if (BuildConfig.DEBUG) {
            binding.iconDone.setBounceClickListener {
                navigateSafe(R.id.stoveSetupCompletedFragment)
            }
        }
        binding.startSetup.setBounceClickListener {
            navigateSafe(
                WelcomeFragmentDirections.actionWelcomeFragmentToStoveSetupBrandFragment(
                    StoveSetupBrandArgs()
                )
            )
//            binding.continueBtn.startAnimation()
            viewModel.setup()
        }

        binding.skipSetup.setBounceClickListener {
            navigateSafe(R.id.action_welcomeFragment_to_dashboardFragment)
        }

    }


    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.loadingLiveData) {
            binding.startSetup.revertAnimation()
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
