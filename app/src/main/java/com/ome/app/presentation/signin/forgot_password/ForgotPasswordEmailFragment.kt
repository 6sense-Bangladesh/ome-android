package com.ome.app.presentation.signin.forgot_password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ome.app.databinding.FragmentForgotPasswordEmailBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.launch.AuthParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordEmailFragment :
    BaseFragment<ForgotPasswordEmailViewModel, FragmentForgotPasswordEmailBinding>(
        FragmentForgotPasswordEmailBinding::inflate
    ) {

    override val viewModel: ForgotPasswordEmailViewModel by viewModels()

    private val args by navArgs<ForgotPasswordEmailFragmentArgs>()
    val params by lazy { args.params }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            closeKeyboard()
            binding.continueBtn.startAnimation()
            viewModel.validateEmail(binding.email.text.toString())
        }

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.forgotPasswordSuccess.collectWithLifecycle(){
            binding.continueBtn.revertAnimation()
            IO { viewModel.pref.setTimer(Constants.VERIFICATION_KEY, Constants.TWO_MINUTES_MILLIS) }
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
