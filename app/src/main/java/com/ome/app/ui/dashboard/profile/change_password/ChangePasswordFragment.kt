package com.ome.app.ui.dashboard.profile.change_password

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.ome.app.R
import com.ome.app.databinding.FragmentChangePasswordBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.errorPassword
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.closeKeyboard
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.onBackPressed
import com.ome.app.utils.toast
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
        }

        binding.topAppBar.setNavigationOnClickListener(::onBackPressed)

        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.validationSuccessFlow.collectWithLifecycle{
            closeKeyboard()
            viewModel.updatePassword()
        }
        viewModel.passwordChangedFlow.collectWithLifecycle{result->
            binding.continueBtn.stopAnimation()
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
