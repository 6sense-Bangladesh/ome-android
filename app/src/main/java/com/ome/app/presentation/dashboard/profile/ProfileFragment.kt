package com.ome.app.presentation.dashboard.profile

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentProfileBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.presentation.base.BaseFragment
import com.ome.app.presentation.dashboard.DashboardFragmentDirections
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun setupUI() {
        binding.softwareVersion.text = "Software Version: ${BuildConfig.VERSION_NAME}"
    }

    override fun setupListener() {
        if (BuildConfig.IS_INTERNAL_TESTING) {
            binding.deleteAccount.makeVisible()
            binding.deleteAccount.setBounceClickListener {
                showDialog(
                    message = SpannableStringBuilder(getString(R.string.confirm_delete_account)),
                    positiveButtonText = "Delete",
                    isRedPositiveButton = true,
                    onPositiveButtonClick = {
                        viewModel.deleteUser{
                            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToLaunchFragment())
                        }
                    })
            }
            binding.avatarIv.setBounceClickListener {
                parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToWelcomeFragment())
            }
        }

        binding.btnChangePassword.setBounceClickListener {
            parentFragment?.navigateSafe(DashboardFragmentDirections.actionDashboardFragmentToChangePasswordFragment())
        }
        binding.btnSave.setBounceClickListener {
            viewModel.updateUserName(
                binding.firstName.text.toString().trim(),
                binding.lastName.text.toString().trim()
            )
            closeKeyboard()
        }
        binding.firstName.doAfterTextChanged {
            binding.firstNameLayout.error = null
        }
        binding.lastName.doAfterTextChanged {
            binding.lastNameLayout.error = null
        }

        binding.lastName.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.closeKeyboard()
                viewModel.updateUserName(
                    binding.firstName.text.toString().trim(),
                    binding.lastName.text.toString().trim()
                )
                return@OnEditorActionListener true
            }
            false
        })
    }


    override fun setupObserver() {
        super.setupObserver()
        mainViewModel.userInfo.collectWithLifecycle{
            it.log("userFlow2")
            binding.firstName.setText(it.firstName)
            binding.lastName.setText(it.lastName)
            binding.email.setText(it.email)
            val fullName = "${it.firstName} ${it.lastName}"
            binding.fullNameTv.text = fullName
        }
        viewModel.validationErrorFlow.collectWithLifecycle {
            when(it.first){
                Validation.FIRST_NAME ->
                    binding.firstNameLayout.error = it.second
                Validation.LAST_NAME ->
                    binding.lastNameLayout.error = it.second
                Validation.ALL_FIELDS -> {
                    binding.firstNameLayout.error = it.second
                    binding.lastNameLayout.error = it.second
                }
                null -> onError(it.second)
                else -> Unit
            }
        }
        viewModel.loadingFlow.collectWithLifecycle {
            if (it)
                binding.btnSave.startAnimation()
            else
                binding.btnSave.revertAnimation()
        }
    }

    override fun handleBackPressEvent() {}

}
