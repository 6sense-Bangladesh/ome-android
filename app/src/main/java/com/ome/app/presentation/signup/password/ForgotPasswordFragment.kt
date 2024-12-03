package com.ome.app.presentation.signup.password

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.gson.annotations.SerializedName
import com.ome.app.R
import com.ome.app.databinding.FragmentForgotPasswordBinding
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.popBackSafe
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
    }

    override fun setupObserver() {
        super.setupObserver()
        subscribe(viewModel.validationSuccessLiveData) {
            if (viewModel.isForgotPassword) {
                viewModel.confirmResetPassword()
            } else {
                viewModel.signUp()
            }
        }

        subscribe(viewModel.passwordResetLiveData) {
            showSuccessDialog(message = getString(R.string.password_reset), onDismiss = {
                popBackSafe(R.id.signInFragment, false)
            })
        }
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
