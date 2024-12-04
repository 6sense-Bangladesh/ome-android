package com.ome.app.presentation.signin

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.amplifyframework.kotlin.core.Amplify
import com.ome.app.R
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.databinding.FragmentSignInBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment :
    BaseFragment<SignInViewModel, FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    override val viewModel: SignInViewModel by viewModels()

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.email.doAfterTextChanged {
            binding.emailLayout.error = null
        }
        binding.password.doAfterTextChanged {
            binding.passwordLayout.error = null
        }
        binding.btnSignIn.setBounceClickListener {
            closeKeyboard()
            val result = viewModel.signIn(binding.email.text.toString(), binding.password.text.toString())
            when(result?.first){
                Validation.EMAIL -> binding.emailLayout.error = result.second
                Validation.OLD_PASSWORD -> binding.passwordLayout.errorPassword = result.second
                else -> Unit
            }
        }
        binding.btnCreateAccount.setBounceClickListener {
            closeKeyboard()
            navigateSafe(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
        binding.btnForgetPassword.setBounceClickListener {
            closeKeyboard()
            navigateSafe(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.loadingFlow.collectWithLifecycle {
            if (it) binding.btnSignIn.startAnimation()
            else binding.btnSignIn.revertAnimation()
        }
        viewModel.destinationAfterSignFlow.collectWithLifecycle {
            AmplifyManager.kotAuth = Amplify.Auth
            //connect to web socket after successful login instantly
            mainViewModel.connectToSocket()
            navigateSafe(it)
        }
    }

}

