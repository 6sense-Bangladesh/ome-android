package com.ome.app.ui.dashboard.profile

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentProfileBinding
import com.ome.app.domain.model.base.Validation
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.closeKeyboard
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.makeVisible
import com.ome.app.utils.setBounceClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()
    private var navController: NavController? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.navHost) as? NavHostFragment
        navController = navHostFragment?.navController
        binding.softwareVersion.text = "Software Version: ${BuildConfig.VERSION_NAME}"
//        viewModel.initUserDataSubscription()

//        binding.titleTv.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true)
//                margin(top = true)
//            }
//        }
//        binding.scroll.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(bottom = true)
//            }
//        }
//        binding.signOut.setBounceClickListener {
//            showDialog(
//                message = SpannableStringBuilder(getString(R.string.confirm_logout)),
//                onPositiveButtonClick = {
//                    binding.signOut.startAnimation()
//                    viewModel.signOut{
//                        navController?.navigate(R.id.action_dashboardFragment_to_launchFragment)
//                    }
//                })
//        }

        if (BuildConfig.DEBUG) {
            binding.deleteAccount.makeVisible()
            binding.deleteAccount.setBounceClickListener {
                showDialog(
                    message = SpannableStringBuilder(getString(R.string.confirm_delete_account)),
                    onPositiveButtonClick = {
                        viewModel.deleteUser{
                            navController?.navigate(R.id.action_dashboardFragment_to_launchFragment)
                        }
                    })
            }
        }

        binding.btnChangePassword.setBounceClickListener {
            navController?.navigate(R.id.action_dashboardFragment_to_changePasswordFragment)
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

//        binding.firstName.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
////                viewModel.updateFirstName(binding.firstName.text.toString().trim())
//                v.closeKeyboard()
//                return@OnEditorActionListener true
//            }
//            false
//        })
//
//        binding.firstName.onFocusChangeListener =
//            OnFocusChangeListener { view, hasFocus ->
//                if (!hasFocus) {
////                    viewModel.updateFirstName(binding.firstName.text.toString().trim())
//                }
//            }


        binding.lastName.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                viewModel.updateLastName(binding.lastName.text.toString().trim())
                v.closeKeyboard()
                viewModel.updateUserName(
                    binding.firstName.text.toString().trim(),
                    binding.lastName.text.toString().trim()
                )
                return@OnEditorActionListener true
            }
            false
        })

//        binding.lastName.onFocusChangeListener =
//            OnFocusChangeListener { view, hasFocus ->
//                if (!hasFocus) {
////                    viewModel.updateLastName(binding.lastName.text.toString().trim())
//                }
//            }

//        binding.avatarIv.setImageDrawable(
//            TextDrawable.builder().beginConfig()
//                .bold()
//                .toUpperCase()
//                .endConfig()
//                .buildRound(
//                    "AS",
//                    ContextCompat.getColor(requireContext(), R.color.gradient_mid_unpressed_color)
//                )
//        )
//        binding.messageIv.setOnClickListener {
//            showMessages()
//        }
    }

    private fun showMessages() {
        val dialog =
            MessagesBottomSheet()
        dialog.onMessageClick = {
            val str = SpannableStringBuilder("\"${it}\" INVITE you  to join in \"Family #1 Stove\"")
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                1,
                it.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            showDialog(
                message = str,
                positiveButtonText = "Accept",
                negativeButtonText = "Reject",
                onPositiveButtonClick = {},
                onNegativeButtonClick = {}
            )
        }
        dialog.show(
            parentFragmentManager,
            "messages_bottom_sheet"
        )
    }


    override fun setupObserver() {
        super.setupObserver()
        mainViewModel.userInfo.collectWithLifecycle{
            binding.firstName.setText(it.firstName)
            binding.lastName.setText(it.lastName)
            binding.email.setText(it.email)
            val fullName = "${it.firstName} ${it.lastName}"
            binding.fullNameTv.text = fullName
//            binding.avatarIv.setImageDrawable(
//                TextDrawable.builder().beginConfig()
//                    .bold()
//                    .toUpperCase()
//                    .endConfig()
//                    .buildRound(
//                        "${if (it.firstName.isNotEmpty()) it.firstName.first() else ""}${if (it.lastName.isNotEmpty()) it.lastName.first() else ""}",
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.gradient_mid_unpressed_color
//                        )
//                    )
//            )
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
