package com.ome.app.base

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {
    val defaultErrorLiveData: SingleLiveEvent<String?> = SingleLiveEvent()
    val loadingFlow = MutableStateFlow(false)

    protected val mainContext: CoroutineContext = Dispatchers.Main
    protected val ioContext: CoroutineContext = Dispatchers.IO


    protected open var defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        sendError(throwable)
    }

    protected fun launch(
        loadingLiveData: MutableStateFlow<Boolean>? = loadingFlow,
        dispatcher: CoroutineContext = mainContext,
        coroutineExceptionHandler: CoroutineExceptionHandler = defaultErrorHandler,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            try {
                withContext(mainContext) {
                    loadingLiveData?.value = true
                }
                this.block()
            } finally {
                withContext(mainContext) {
                    loadingLiveData?.value = false
                }
            }
        }
    }

    protected fun sendError(throwable: Throwable) {
        launch(dispatcher = mainContext) {
            defaultErrorLiveData.setNewValue(throwable.message)
        }
    }

    @MainThread
    protected fun <T : Any?> LiveData<T>.setNewValue(newValue: T) {
        when (this) {
            is MutableLiveData -> this.value = newValue
            else -> throw Exception("Not using createMutableLiveData() or createSingleLiveData() to create live data")
        }
    }

}
