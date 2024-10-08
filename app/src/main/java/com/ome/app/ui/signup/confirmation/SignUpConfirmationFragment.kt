package com.ome.app.ui.signup.confirmation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amplifyframework.kotlin.core.Amplify
import com.ome.Ome.R
import com.ome.Ome.databinding.FragmentSignUpConfirmationBinding
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.applyMaskToEmail
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter


@AndroidEntryPoint
class SignUpConfirmationFragment :
    BaseFragment<SignUpConfirmationViewModel, FragmentSignUpConfirmationBinding>(
        FragmentSignUpConfirmationBinding::inflate
    ) {

    override val viewModel: SignUpConfirmationViewModel by viewModels()

    private val args by navArgs<SignUpConfirmationFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.continueBtn.setOnClickListener {
            binding.continueBtn.startAnimation()
            viewModel.validateConfirmationCode(binding.enterCode.getText())
        }

        binding.resendCode.setOnClickListener {
            viewModel.resendCode(viewModel.email.trim())
        }

        binding.textLabel.text =
            getString(R.string.confirmation_label, args.params.email.applyMaskToEmail())
        viewModel.firstName = args.params.firstName
        viewModel.lastName = args.params.lastName
        viewModel.email = args.params.email
        viewModel.phone = args.params.phone
        viewModel.currentPassword = args.params.currentPassword
        viewModel.isForgotPassword = args.params.isForgotPassword
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.signUpConfirmationResultLiveData) {
            binding.continueBtn.revertAnimation()
            if (it) {
                AmplifyManager.kotAuth = Amplify.Auth
                findNavController().navigate(
                    SignUpConfirmationFragmentDirections.actionSignUpConfirmationFragmentToDashboardFragment()
                )
            }

        }
        subscribe(viewModel.resendClickedResultLiveData) {
            showSuccessDialog(
                message = getString(
                    R.string.confirmation_label_dialog,
                    viewModel.email.applyMaskToEmail()
                )
            )
        }

        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
        }


        subscribe(viewModel.codeValidationLiveData) {
            if (viewModel.isForgotPassword) {
                findNavController().navigate(
                    SignUpConfirmationFragmentDirections.actionSignUpConfirmationFragmentToSignUpPasswordFragment(
                        args.params.apply { code = viewModel.code }
                    )
                )
            } else {
                viewModel.confirmSignUp()
            }
        }

        subscribe(viewModel.loadingLiveData) {
            binding.continueBtn.revertAnimation()
        }
    }

}
