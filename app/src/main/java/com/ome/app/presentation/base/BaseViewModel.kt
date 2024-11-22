package com.ome.app.presentation.base

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.amplifyframework.auth.AuthException
import com.ome.app.data.remote.error
import com.ome.app.utils.isTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel: ViewModel() {

    val defaultErrorLiveData: SingleLiveEvent<String?> = SingleLiveEvent()
    val successMessageLiveData: SingleLiveEvent<String?> = SingleLiveEvent()
    val successToastFlow= MutableSharedFlow<String?>()
    val loadingFlow = MutableSharedFlow<Boolean>()

    val loadingLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val currentDestination = MutableStateFlow<NavDestination?>(null)

    protected val mainContext: CoroutineContext = Dispatchers.Main
    protected val ioContext: CoroutineContext = Dispatchers.IO

    protected open var defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        loadingLiveData.postValue(false)
        if (throwable is AuthException) sendError(Throwable(throwable.error.replace("username or ", "")))
        else sendError(throwable)
    }

    protected fun launch(
        dispatcher: CoroutineContext = mainContext,
        showLoading: Boolean = true,
        coroutineExceptionHandler: CoroutineExceptionHandler = defaultErrorHandler,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            try {
                if(showLoading) loadingFlow.emit(true)
                block()
            } finally {
                if(showLoading) loadingFlow.emit(false)
            }
        }
    }

    private fun sendError(throwable: Throwable) {
        launch(mainContext) {
            throwable.printStackTrace()
            if(throwable.message?.contains("handler", true).isTrue()) return@launch
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
