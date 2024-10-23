package com.ome.app.domain.model.base

sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T): com.ome.app.domain.model.base.ResponseWrapper<T>()
    data class Error(val message: String, val type: com.ome.app.domain.model.base.ErrorType = com.ome.app.domain.model.base.ErrorType.DEFAULT, val code: Int = -1): com.ome.app.domain.model.base.ResponseWrapper<Nothing>()

    val isSuccess: Boolean
        get() = this is com.ome.app.domain.model.base.ResponseWrapper.Success

    val isError: Boolean
        get() = this is com.ome.app.domain.model.base.ResponseWrapper.Error

    fun getOrNull(): T? =
        when(this){
            is com.ome.app.domain.model.base.ResponseWrapper.Success -> value
            is com.ome.app.domain.model.base.ResponseWrapper.Error -> null
        }
}

enum class ErrorType(val message: String){
    DEFAULT("Something went wrong"),
    NETWORK("Server not responding"),
    NO_INTERNET("No internet connection")
}
