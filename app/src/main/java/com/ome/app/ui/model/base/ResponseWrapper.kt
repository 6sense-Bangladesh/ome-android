package com.ome.app.ui.model.base

sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T): ResponseWrapper<T>()
    data class Error(val message: String, val type: ErrorType = ErrorType.DEFAULT, val code: Int = -1): ResponseWrapper<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun getOrNull(): T? =
        when(this){
            is Success -> value
            is Error -> null
        }
}

enum class ErrorType(val message: String){
    DEFAULT("Something went wrong"),
    NETWORK("Server not responding"),
    NO_INTERNET("No internet connection")
}
