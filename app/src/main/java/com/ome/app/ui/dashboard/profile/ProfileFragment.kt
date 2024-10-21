package com.ome.app.ui.dashboard.profile

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.amulyakhare.textdrawable.TextDrawable
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.databinding.FragmentProfileBinding
import com.ome.app.ui.base.BaseFragment
import com.ome.app.utils.makeVisible
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    override val viewModel: ProfileViewModel by viewModels()
    private var navController: NavController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.navHost) as? NavHostFragment
        navController = navHostFragment?.navController
        viewModel.initUserDataSubscription()

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
        binding.signOut.setBounceClickListener {
            showDialog(
                message = SpannableStringBuilder(getString(R.string.confirm_logout)),
                onPositiveButtonClick = {
                    binding.signOut.startAnimation()
                    viewModel.signOut{
                        navController?.navigate(R.id.action_dashboardFragment_to_launchFragment)
//                        navController?.navigate(R.id.launchFragment)
//                        activity?.finish()
                    }
                })
        }

        if (BuildConfig.DEBUG) {
            binding.deleteAccount.makeVisible()
            binding.deleteAccount.setBounceClickListener {
                showDialog(
                    message = SpannableStringBuilder(getString(R.string.confirm_delete_account)),
                    onPositiveButtonClick = {
                        binding.deleteAccount.startAnimation()
                        viewModel.deleteUser{
                            navController?.navigate(R.id.action_dashboardFragment_to_launchFragment)
//                            navController?.navigate(R.id.launchFragment)
//                            activity?.finish()
                        }
                    })
            }
        }

        binding.changePasswordTv.setBounceClickListener {
            navController?.navigate(R.id.action_dashboardFragment_to_changePasswordFragment)
        }

        binding.firstNameEt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateFirstName(binding.firstNameEt.text.toString().trim())
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        binding.firstNameEt.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    viewModel.updateFirstName(binding.firstNameEt.text.toString().trim())
                }
            }


        binding.lastNameEt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateLastName(binding.lastNameEt.text.toString().trim())
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        binding.lastNameEt.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    viewModel.updateLastName(binding.lastNameEt.text.toString().trim())
                }
            }

        binding.avatarIv.setImageDrawable(
            TextDrawable.builder().beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(
                    "AS",
                    ContextCompat.getColor(requireContext(), R.color.gradient_mid_unpressed_color)
                )
        )
        binding.messageIv.setOnClickListener {
            showMessages()
        }
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

    fun hideKeyboard(){
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun observeLiveData() {
        super.observeLiveData()
        subscribe(viewModel.userLiveData) {
            binding.firstNameEt.setText(it.firstName)
            binding.lastNameEt.setText(it.lastName)
            binding.emailEt.setText(it.email)
            val fullName = "${it.firstName} ${it.lastName}"
            binding.fullNameTv.text = fullName
            binding.avatarIv.setImageDrawable(
                TextDrawable.builder().beginConfig()
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(
                        "${if (it.firstName.isNotEmpty()) it.firstName.first() else ""}${if (it.lastName.isNotEmpty()) it.lastName.first() else ""}",
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gradient_mid_unpressed_color
                        )
                    )
            )

        }
    }

    override fun handleBackPressEvent() {}

}
