package com.ome.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ome.app.utils.showToast
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
    }

    protected open fun onError(errorMessage: String) {
        showToast(errorMessage)
    }

}
