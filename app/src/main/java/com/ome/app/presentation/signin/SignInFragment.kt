package com.ome.app.presentation.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amplifyframework.kotlin.core.Amplify
import com.ome.app.R
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.databinding.FragmentSignInBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.closeKeyboard
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class SignInFragment :
    BaseFragment<SignInViewModel, FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    override val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }

        binding.continueBtn.setOnClickListener {
            binding.continueBtn.startAnimation()
            viewModel.signIn(binding.email.getText(), binding.password.getText())
            closeKeyboard()
        }

//        binding.deleteBtn.setOnClickListener {
//            binding.deleteBtn.startAnimation()
//            viewModel.deleteUser()
//        }

        binding.forgotPassword.setOnClickListener {
            navigateSafe(R.id.action_signInFragment_to_forgotPasswordFragment)
        }

    }

    override fun setupObserver() {
        super.setupObserver()

        subscribe(viewModel.signInStatus) {
            if (it) {
                viewModel.fetchUserData()
            }
        }
//        subscribe(viewModel.deleteStatus) {
//            binding.deleteBtn.revertAnimation()
//        }
//
        subscribe(viewModel.loadingLiveData) {
            //binding.deleteBtn.revertAnimation()
            binding.continueBtn.revertAnimation()
        }

        subscribe(viewModel.destinationAfterSignInLiveData){
            binding.continueBtn.revertAnimation()
            AmplifyManager.kotAuth = Amplify.Auth
            navigateSafe(it)
        }
    }

}

