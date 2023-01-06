package com.ome.app.ui.signup.password

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ome.app.R
import com.ome.app.base.BaseFragment
import com.ome.app.databinding.FragmentSignUpPasswordBinding
import com.ome.app.utils.applyMaskToEmail
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class SignUpPasswordFragment :
    BaseFragment<SignUpPasswordViewModel, FragmentSignUpPasswordBinding>(
        FragmentSignUpPasswordBinding::inflate
    ) {

    override val viewModel: SignUpPasswordViewModel by viewModels()

    private val args by navArgs<SignUpPasswordFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIv.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.continueBtn.setOnClickListener {
            viewModel.validatePassword(
                binding.enterPassword.getText(),
                binding.confirmPassword.getText()
            )
        }
        viewModel.email = args.params.email
        viewModel.phone = args.params.phone
        viewModel.code = args.params.code
        viewModel.firstName = args.params.firstName
        viewModel.lastName = args.params.lastName
        viewModel.isForgotPassword = args.params.isForgotPassword

        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.setLinkTextColor(Color.WHITE)

    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.isPasswordValidLiveData) {
            if (viewModel.isForgotPassword) {
                viewModel.confirmResetPassword()
            } else {
                viewModel.signUp()
            }
        }
        subscribe(viewModel.signUpResultLiveData) {
            showSuccessDialog(message = getString(R.string.confirmation_label_dialog, viewModel.email.applyMaskToEmail()), onDismiss = {
                findNavController().navigate(
                    SignUpPasswordFragmentDirections.actionSignUpPasswordFragmentToSignUpConfirmationFragment(
                        AuthParams(
                            firstName = viewModel.firstName,
                            lastName = viewModel.lastName,
                            currentPassword = viewModel.currentPassword,
                            email = viewModel.email,
                            phone = viewModel.phone
                        )
                    )
                )
            })

        }

        subscribe(viewModel.passwordResetLiveData){
            showSuccessDialog(message = getString(R.string.password_reset), onDismiss = {
                findNavController().popBackStack(R.id.signInFragment, false)
            })
        }
    }

}

@Parcelize
data class AuthParams(
    val firstName: String = "",
    val lastName: String = "",
    val currentPassword: String = "",
    val email: String = "",
    val phone: String = "",
    var code: String = "",
    val isForgotPassword: Boolean = false
) : Parcelable
