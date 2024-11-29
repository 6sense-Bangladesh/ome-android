package com.ome.app.presentation.launch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.BuildConfig
import com.ome.app.databinding.FragmentLaunchBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.presentation.signup.password.AuthParams
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchFragment :
    BaseFragment<EmptyViewModel, FragmentLaunchBinding>(FragmentLaunchBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountBtn.setBounceClickListener {
            navigateSafe(LaunchFragmentDirections.actionWelcomeFragmentToSignUpFragment())
        }
        binding.signInBtn.setBounceClickListener {
            navigateSafe(LaunchFragmentDirections.actionLaunchFragmentToSignInFragment())
        }
        if(BuildConfig.DEBUG){
            binding.imageView7.setBounceClickListener {
                navigateSafe(LaunchFragmentDirections.actionLaunchFragmentToVerificationFragment(AuthParams()))
            }
        }
    }
}
