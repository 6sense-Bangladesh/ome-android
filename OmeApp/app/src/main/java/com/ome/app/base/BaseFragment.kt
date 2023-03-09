package com.ome.app.base

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.ome.app.MainVM
import com.ome.app.ui.dashboard.members.MembersFragment
import com.ome.app.ui.dashboard.mystove.MyStoveFragment
import com.ome.app.ui.dashboard.profile.ProfileFragment
import com.ome.app.ui.dashboard.settings.SettingsFragment
import com.ome.app.ui.launch.LaunchFragment
import com.ome.app.ui.signup.welcome.WelcomeFragment
import com.ome.app.utils.subscribe


abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding>(
    factory: (LayoutInflater) -> VB
) : Fragment() {

    protected abstract val viewModel: VM
    protected val binding by viewBindingAlt(factory)

    protected val mainViewModel: MainVM by activityViewModels()
    protected var onDismissErrorDialog: () -> Unit = {}
    protected var onDismissSuccessDialog: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressEvent()
        observeLiveData()
    }

    @CallSuper
    open fun observeLiveData() {
        subscribe(viewModel.defaultErrorLiveData) { message ->
            message?.let {
                onError(it)
            }
        }

        subscribe(viewModel.successMessageLiveData) { message ->
            message?.let {
                showSuccessDialog(message = it)
            }
        }
    }


    open fun handleBackPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (this@BaseFragment) {
                    is ProfileFragment,
                    is MyStoveFragment,
                    is WelcomeFragment,
                    is MembersFragment,
                    is SettingsFragment,
                    is LaunchFragment -> {
                        requireActivity().finishAndRemoveTask()
                    }
                    else -> {
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }


    fun setStatusBarTheme(isLight: Boolean = true) {
        view?.let {
            WindowInsetsControllerCompat(requireActivity().window, it).isAppearanceLightStatusBars =
                isLight
        }
    }

    protected open fun showSuccessDialog(
        title: String = "Success",
        message: String,
        onDismiss: () -> Unit = {}
    ): AlertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            "Ok"
        ) { dialog, p1 ->
            onDismissSuccessDialog()
            onDismiss()
            dialog.cancel()
        }
        .show()

    protected open fun showDialog(
        title: String = "",
        positiveButtonText: String = "Ok",
        negativeButtonText: String = "Cancel",
        message: SpannableStringBuilder,
        onPositiveButtonClick: () -> Unit = {},
        onNegativeButtonClick: () -> Unit = {}
    ): AlertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            positiveButtonText
        ) { dialog, p1 ->
            onPositiveButtonClick()
        }.setNegativeButton(negativeButtonText) { dialog, p1 ->
            onNegativeButtonClick
        }
        .show()

    protected open fun onError(errorMessage: String) = AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(errorMessage)
        .setPositiveButton(
            "Ok"
        ) { dialog, p1 ->
            onDismissErrorDialog()
            dialog.cancel()
        }
        .show()

}
