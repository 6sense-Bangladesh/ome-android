package com.ome.app.ui.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentSignInBinding
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
        }

//        binding.deleteBtn.setOnClickListener {
//            binding.deleteBtn.startAnimation()
//            viewModel.deleteUser()
//        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }

    }

    override fun observeLiveData() {
        super.observeLiveData()

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
            findNavController().navigate(it.first)
        }
    }

}

