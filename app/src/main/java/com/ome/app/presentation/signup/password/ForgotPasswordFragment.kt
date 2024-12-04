package com.ome.app.presentation.signup.password

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.gson.annotations.SerializedName
import com.ome.app.R
import com.ome.app.databinding.FragmentForgotPasswordBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.popBackSafe
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseFragment<SignUpPasswordViewModel, FragmentForgotPasswordBinding>(
        FragmentForgotPasswordBinding::inflate
    ) {

    override val viewModel: SignUpPasswordViewModel by viewModels()

    private val args by navArgs<ForgotPasswordFragmentArgs>()
    val params by lazy { args.params }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        viewModel.email = params.email
        viewModel.code = params.code
    }

    override fun setupUI() {
        binding.password.doAfterTextChanged {
            binding.passwordLayout.errorPassword = null
        }
        binding.retypePassword.doAfterTextChanged {
            binding.retypePasswordLayout.errorPassword = null
        }
    }

    override fun setupListener() {
        binding.btnReset.setBounceClickListener {
            viewModel.validateFields(
                currentPassword = binding.password.text.toString(),
                retypePassword = binding.retypePassword.text.toString(),
            )
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validationErrorFlow.collectWithLifecycle { validationList ->
            validationList.forEach {
                when (it.first) {
                    Validation.NEW_PASSWORD -> binding.passwordLayout.errorPassword = it.second
                    Validation.RE_PASSWORD -> binding.retypePasswordLayout.errorPassword = it.second
                    else -> Unit
                }
            }
        }
        subscribe(viewModel.validationSuccessFlow){
            if(it.isSuccessful) {
                showSuccessDialog(
                    message = getString(R.string.password_reset),
                    onDismiss = {
                        popBackSafe(R.id.signInFragment, false)
                    }
                )
            }
        }

        //previous implementation of forgot password dialog and pop back
      /*  subscribe(viewModel.passwordResetLiveData) {
            showSuccessDialog(message = getString(R.string.password_reset), onDismiss = {
                popBackSafe(R.id.signInFragment, false)
            })
        }*/
    }

}

@Parcelize
data class AuthParams(
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("current_password") val currentPassword: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("code") var code: String = "",
    @SerializedName("is_forgot_password") val isForgotPassword: Boolean = false
) : Parcelable
