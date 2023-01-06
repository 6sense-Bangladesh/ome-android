package com.ome.app.ui.signup.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentWelcomeBinding
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
        binding.continueBtn.setOnClickListener {
//            binding.continueBtn.startAnimation()
            viewModel.setup()
        }

    }


    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
        }
        subscribe(viewModel.fetchUserDataStatus) {
//            findNavController().navigate(R.id.action_welcomeFragment_to_stoveSetupBrandFragment)
        }
    }
}
