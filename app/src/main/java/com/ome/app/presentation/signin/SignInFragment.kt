package com.ome.app.presentation.signin

import androidx.fragment.app.viewModels
import com.amplifyframework.kotlin.core.Amplify
import com.ome.app.R
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.databinding.FragmentSignInBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment :
    BaseFragment<SignInViewModel, FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    override val viewModel: SignInViewModel by viewModels()

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.btnSignIn.setBounceClickListener {
            closeKeyboard()
            binding.btnSignIn.startAnimation()
            viewModel.signIn(binding.email.text.toString(), binding.password.text.toString())
        }
        binding.btnCreateAccount.setBounceClickListener {
            closeKeyboard()
            navigateSafe(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
        onDismissErrorDialog = {
            binding.btnSignIn.revertAnimation()
        }
        binding.btnForgetPassword.setBounceClickListener {
            closeKeyboard()
            navigateSafe(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        subscribe(viewModel.signInStatus) {
            if (it) viewModel.fetchUserData()
        }

        subscribe(viewModel.destinationAfterSignInLiveData){
            AmplifyManager.kotAuth = Amplify.Auth
            //connect to web socket after successful login instantly
            mainViewModel.connectToSocket()
            navigateSafe(it)
        }
    }

}

