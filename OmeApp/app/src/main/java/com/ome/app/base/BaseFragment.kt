package com.ome.app.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ome.app.utils.subscribe


abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding>(
    factory: (LayoutInflater) -> VB
) : Fragment() {

    protected abstract val viewModel: VM
    protected val binding by viewBindingAlt(factory)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


    fun setStatusBarColor(isLight: Boolean = true) {
        view?.let {
            WindowInsetsControllerCompat(requireActivity().window, it).isAppearanceLightStatusBars = isLight
        }
    }

    protected open fun showSuccessDialog(
        title: String = "Success",
        message: String,
        onDismiss: () -> Unit = {}
    ) =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Ok"
            ) { dialog, p1 ->
                onDismiss()
                dialog.cancel()
            }
            .show()

    protected open fun onError(errorMessage: String) = AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(errorMessage)
        .setPositiveButton(
            "Ok"
        ) { dialog, p1 ->
            dialog.cancel()
        }
        .show()

}
