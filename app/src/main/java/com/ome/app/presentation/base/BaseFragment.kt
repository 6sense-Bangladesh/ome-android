package com.ome.app.presentation.base

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
import com.ome.app.R
import com.ome.app.presentation.dashboard.members.MembersFragment
import com.ome.app.presentation.dashboard.my_stove.MyStoveFragment
import com.ome.app.presentation.dashboard.profile.ProfileFragment
import com.ome.app.presentation.dashboard.settings.SettingsFragment
import com.ome.app.presentation.launch.LaunchFragment
import com.ome.app.presentation.signup.welcome.WelcomeFragment
import com.ome.app.presentation.views.ProgressDialog
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

    private val loader by lazy { ProgressDialog.create(context)}

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

    fun showLoader() = loader?.show()
    fun hideLoader() = loader?.dismiss()

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
                getString(R.string.continue_btn)
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

    protected open fun onError(errorMessage: String?, title: String = "Warning") = context?.let {
        MaterialAlertDialogBuilder(it)
            .setTitle(title)
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
