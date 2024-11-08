package com.ome.app.presentation.launch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentLaunchBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.base.EmptyViewModel
import com.ome.app.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchFragment :
    BaseFragment<EmptyViewModel, FragmentLaunchBinding>(FragmentLaunchBinding::inflate) {

    override val viewModel: EmptyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createAccountBtn.setOnClickListener {
            navigateSafe(LaunchFragmentDirections.actionWelcomeFragmentToSignUpNameFragment())
        }
        binding.signInBtn.setOnClickListener {
            navigateSafe(LaunchFragmentDirections.actionWelcomeFragmentToSignInFragment())
        }
    }


}
