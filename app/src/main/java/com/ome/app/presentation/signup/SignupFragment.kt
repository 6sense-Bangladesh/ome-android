package com.ome.app.presentation.signup

import android.os.Parcelable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentSignupBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.signup.password.AuthParams
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class SignupFragment : BaseFragment<SignupViewModel, FragmentSignupBinding>(
    FragmentSignupBinding::inflate
) {
    override val viewModel: SignupViewModel by viewModels()

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validationErrorFlow.collectWithLifecycle {validationList->
            validationList.forEach{
                when (it.first) {
                    Validation.FIRST_NAME -> binding.firstNameLayout.error = it.second
                    Validation.LAST_NAME -> binding.lastNameLayout.error = it.second
                    Validation.NEW_PASSWORD -> binding.passwordLayout.errorPassword = it.second
                    Validation.RE_PASSWORD -> binding.retypePasswordLayout.errorPassword = it.second
                    Validation.EMAIL -> binding.emailLayout.error = it.second
                    Validation.PHONE -> binding.phoneInputLayout.error = it.second
                    else -> Unit
                }
            }
        }
    }

    override fun setupUI() {
        binding.firstName.doAfterTextChanged {
            binding.firstNameLayout.error = null
        }
        binding.lastName.doAfterTextChanged {
            binding.lastNameLayout.error = null
        }
        binding.email.doAfterTextChanged {
            binding.emailLayout.error = null
        }
        binding.phoneInput.doAfterTextChanged {
            binding.phoneInputLayout.error = null
        }
        binding.password.doAfterTextChanged {
            binding.passwordLayout.errorPassword = null
        }
        binding.retypePassword.doAfterTextChanged {
            binding.retypePasswordLayout.errorPassword = null
        }
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.signupButton.setBounceClickListener {
            viewModel.validateFields(
                firstName = binding.firstName.text.toString(),
                lastName = binding.lastName.text.toString(),
                email = binding.email.text.toString(),
                phone = binding.phoneInput.text.toString(),
                password = binding.password.text.toString(),
                confirmPassword = binding.retypePassword.text.toString(),
            )
        }

        viewModel.loadingFlow.collectWithLifecycle {
            if (it) {
                binding.signupButton.startAnimation()
            } else {
                binding.signupButton.revertAnimation()
            }
        }
        viewModel.validationSuccessFlow.collectWithLifecycle {
            if(it.isSuccessful) {
                viewModel.pref.setTimer(Constants.VERIFICATION_KEY, Constants.TWO_MINUTES_MILLIS)
                navigateSafe(SignupFragmentDirections.actionSignUpFragmentToVerificationFragment(
                    params = AuthParams(
                        firstName = viewModel.firstName,
                        lastName = viewModel.lastName,
                        email = viewModel.email,
                        phone = viewModel.phone,
                        currentPassword = viewModel.password
                    )
                ))
            }
        }
        binding.signinButton.setBounceClickListener{
            navigateSafe(SignupFragmentDirections.actionSignUpFragmentToSignInFragment())
        }
    }
}


@Parcelize
data class SignupParams(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) : Parcelable