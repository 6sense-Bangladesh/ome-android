package com.ome.app.ui.signin.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentForgotPasswordEmailBinding
import com.ome.app.ui.signup.password.AuthParams
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseFragment<ForgotPasswordViewModel, FragmentForgotPasswordEmailBinding>(
        FragmentForgotPasswordEmailBinding::inflate
    ) {

    override val viewModel: ForgotPasswordViewModel by viewModels()

    private val args by navArgs<ForgotPasswordFragmentArgs>()

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
            viewModel.validateEmail(binding.email.getText())
        }

        binding.backToLoginBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.forgotPasswordSuccess) {
            binding.continueBtn.revertAnimation()
            findNavController().navigate(
                ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignUpConfirmationFragment(
                    AuthParams(isForgotPassword = true, email = binding.email.getText())
                )
            )
        }
        subscribe(viewModel.loadingLiveData){
            binding.continueBtn.revertAnimation()
        }
        subscribe(viewModel.emailAndPassValidationLiveData) {
            viewModel.forgotPassword()
        }
    }

}
