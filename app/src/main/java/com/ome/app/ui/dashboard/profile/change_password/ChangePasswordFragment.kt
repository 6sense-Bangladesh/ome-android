package com.ome.app.ui.dashboard.profile.change_password

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.ome.app.R
import com.ome.app.databinding.FragmentChangePasswordBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.gone
import com.ome.app.utils.isValidPasswordResult
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.subscribe
import com.ome.app.utils.toast
import com.ome.app.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment:  BaseFragment<ChangePasswordViewModel, FragmentChangePasswordBinding>(
    FragmentChangePasswordBinding::inflate
) {

    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.backIv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
//        binding.backIv.setOnClickListener {
//            findNavController().popBackStack()
//        }
        binding.continueBtn.setOnClickListener {
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
            if(it.toString().trim().isNotEmpty()){
                when (val validator = it.toString().isValidPasswordResult()) {
                    is com.ome.app.domain.model.base.ResponseWrapper.Success -> binding.continueBtn.isEnabled = true
                    is com.ome.app.domain.model.base.ResponseWrapper.Error -> {
                        binding.newPasswordLayout.error = validator.message
                        binding.newPasswordLayout.errorIconDrawable = null
                        binding.newPasswordLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                        binding.continueBtn.isEnabled = false
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.validationSuccessFlow.collectWithLifecycle{
            viewModel.updatePassword()
        }
        viewModel.passwordChangedFlow.collectWithLifecycle{
            binding.loadingLayout.root.gone()
            toast("Password changed successfully")
            onBackPressed()
        }
        viewModel.validationErrorFlow.collectWithLifecycle{
            when(it.first){
                ChangePasswordViewModel.Validation.OLD_PASSWORD -> {
                    binding.oldPasswordLayout.error = it.second
                    binding.oldPasswordLayout.errorIconDrawable = null
                    binding.oldPasswordLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                ChangePasswordViewModel.Validation.NEW_PASSWORD -> {
                    binding.newPasswordLayout.error = it.second
                    binding.newPasswordLayout.errorIconDrawable = null
                    binding.newPasswordLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                null -> toast(it.second)
            }
        }
        subscribe(viewModel.loadingLiveData){
            if (it){
                binding.loadingLayout.root.visible()
            }else{
                binding.loadingLayout.root.gone()
            }
        }
    }

}
