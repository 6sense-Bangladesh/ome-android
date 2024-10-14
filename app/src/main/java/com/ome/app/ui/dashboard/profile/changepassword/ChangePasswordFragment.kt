package com.ome.app.ui.dashboard.profile.changepassword

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ome.app.databinding.FragmentChangePasswordBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class ChangePasswordFragment:  BaseFragment<ChangePasswordViewModel, FragmentChangePasswordBinding>(
    FragmentChangePasswordBinding::inflate
) {

    override val viewModel: ChangePasswordViewModel by viewModels()

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
                binding.oldPassword.getText(),
                binding.newPassword.getText()
            )
        }

        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.setLinkTextColor(Color.WHITE)

    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.validationSuccessLiveData){
            viewModel.updatePassword()
        }
        subscribe(viewModel.passwordChangedLiveData){
           findNavController().popBackStack()
        }
    }

}
