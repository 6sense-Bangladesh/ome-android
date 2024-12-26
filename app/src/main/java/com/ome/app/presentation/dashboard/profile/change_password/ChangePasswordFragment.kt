package com.ome.app.presentation.dashboard.profile.change_password

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.ome.app.databinding.FragmentChangePasswordBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment:  BaseFragment<ChangePasswordViewModel, FragmentChangePasswordBinding>(
    FragmentChangePasswordBinding::inflate
) {

    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)
        binding.continueBtn.setBounceClickListener {
            viewModel.validatePassword(
                binding.oldPassword.text.toString(),
                binding.newPassword.text.toString()
            )
        }
        binding.oldPassword.doAfterTextChanged {
            binding.oldPasswordLayout.error = null
        }
        binding.newPassword.doAfterTextChanged {
            binding.newPasswordLayout.error = null
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.validationSuccessFlow.collectWithLifecycle{
            closeKeyboard()
            viewModel.updatePassword()
        }
        viewModel.passwordChangedFlow.collectWithLifecycle{result->
            if(result.isSuccessful){
                toast(result.message)
                onBackPressed()
            }
            else
                onError(result.message)
        }
        viewModel.validationErrorFlow.collectWithLifecycle{
            when(it.first){
                Validation.OLD_PASSWORD ->
                    binding.oldPasswordLayout.errorPassword = it.second
                Validation.NEW_PASSWORD ->
                    binding.newPasswordLayout.errorPassword = it.second
                Validation.ALL_FIELDS ->{
                    binding.oldPasswordLayout.errorPassword = it.second
                    binding.newPasswordLayout.errorPassword = it.second
                }
                else -> Unit
            }
        }
        viewModel.loadingFlow.collectWithLifecycle{
            if (it)
                binding.continueBtn.startAnimation()
            else
                binding.continueBtn.revertAnimation()
        }
    }

}
