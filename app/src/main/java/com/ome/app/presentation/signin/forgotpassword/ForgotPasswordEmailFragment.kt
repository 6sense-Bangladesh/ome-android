package com.ome.app.presentation.signin.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentForgotPasswordEmailBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.signup.password.AuthParams
import com.ome.app.utils.navigateSafe
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordEmailFragment :
    BaseFragment<ForgotPasswordViewModel, FragmentForgotPasswordEmailBinding>(
        FragmentForgotPasswordEmailBinding::inflate
    ) {

    override val viewModel: ForgotPasswordViewModel by viewModels()

    private val args by navArgs<ForgotPasswordEmailFragmentArgs>()
    val params by lazy { args.params }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            binding.continueBtn.startAnimation()
            viewModel.validateEmail(binding.email.text.toString())
        }

    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.forgotPasswordSuccess) {
            binding.continueBtn.revertAnimation()
            navigateSafe(
                ForgotPasswordEmailFragmentDirections.actionForgotPasswordEmailFragmentToVerificationFragment(
                    AuthParams(isForgotPassword = true, email = binding.email.text.toString())
                )
            )
        }
        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
        }
        subscribe(viewModel.emailAndPassValidationLiveData) {
            viewModel.forgotPassword()
        }
    }

}
