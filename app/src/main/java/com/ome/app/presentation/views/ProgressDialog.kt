package com.ome.app.presentation.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.ome.app.databinding.DialogCustomLoaderBinding
import com.ome.app.utils.tryGet
import kotlinx.coroutines.*

class ProgressDialog(builder: Builder) : Dialog(builder.context) {
    private var _binding: DialogCustomLoaderBinding? = null
    private val binding
        get() = _binding!!

    private val timeOutDuration = 20000L
    private var timeOutJob : Job? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

    init {
        _binding = DialogCustomLoaderBinding.inflate(LayoutInflater.from(builder.context))
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(builder.cancelable)
        setContentView(binding.root)
        // Set the timeout to hide the dialog after 20 seconds
        timeOutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(timeOutDuration)
            if (isShowing) {
                dismiss()
            }
        }
    }

    class Builder(val context: Context) {

        var cancelable: Boolean = false

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun build(): ProgressDialog {
            return ProgressDialog(this)
        }
    }

    override fun dismiss() {
        runCatching {
            super.dismiss()
            timeOutJob?.cancel()
        }
    }
    companion object {
        fun create(context: Context?) = context?.let { Builder(it).build() }
    }
}