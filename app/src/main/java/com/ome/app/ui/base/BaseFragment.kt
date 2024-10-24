package com.ome.app.ui.base

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ome.app.MainVM
import com.ome.app.ui.dashboard.members.MembersFragment
import com.ome.app.ui.dashboard.my_stove.MyStoveFragment
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
    protected val isFromDeepLink by lazy { arguments?.containsKey(NavController.KEY_DEEP_LINK_INTENT) ?: false }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressEvent()
        setupListener()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    open fun setupUI() = Unit
    open fun setupListener() = Unit

    @CallSuper
    open fun setupObserver() {
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
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                when (this@BaseFragment) {
                    is ProfileFragment,
                    is MyStoveFragment,
                    is WelcomeFragment,
                    is MembersFragment,
                    is SettingsFragment,
                    is LaunchFragment -> {
                        activity?.finishAndRemoveTask()
                    }

                    else -> {
                        findNavController().popBackStack()
                    }
                }
            }
    }


    fun setStatusBarAppearance(isLight: Boolean = true) {
        view?.let {
            WindowInsetsControllerCompat(requireActivity().window, it).isAppearanceLightStatusBars =
                isLight
        }
    }

    protected open fun showSuccessDialog(
        title: String = "Success",
        message: String,
        onDismiss: () -> Unit = {}
    ) = context?.let {
        MaterialAlertDialogBuilder(it)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Okay"
            ) { dialog, _ ->
                onDismissSuccessDialog()
                onDismiss()
                dialog.cancel()
            }
            .show()
    }

    protected open fun showDialog(
        title: String = "Confirmation",
        positiveButtonText: String = "Okay",
        negativeButtonText: String = "Cancel",
        isRedPositiveButton: Boolean = false,
        message: SpannableStringBuilder,
        onPositiveButtonClick: () -> Unit = {},
        onNegativeButtonClick: () -> Unit = {}
    ) = context?.let {
        MaterialAlertDialogBuilder(it)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveButtonClick()
            }.setNegativeButton(negativeButtonText) { _, _ ->
                onNegativeButtonClick()
            }.create().apply {
                show()
                if (isRedPositiveButton) {
                    val pBtn = getButton(DialogInterface.BUTTON_POSITIVE)
                    pBtn?.setTextColor(Color.RED)
                }
            }
    }

    protected open fun onError(errorMessage: String?) = context?.let {
        MaterialAlertDialogBuilder(it)
            .setTitle("Warning")
            .setMessage(errorMessage ?: "Something went wrong.")
            .setPositiveButton(
                "Close"
            ) { dialog, _ ->
                onDismissErrorDialog()
                dialog.cancel()
            }
            .show()
    }

}
