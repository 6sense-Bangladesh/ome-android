package com.ome.app.model.base

import com.ome.app.data.remote.base.ErrorResponse

sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T): ResponseWrapper<T>()
    data class GenericError(val code: Int? = null, val response: ErrorResponse? = null): ResponseWrapper<Nothing>()
    object NetworkError: ResponseWrapper<Nothing>()
}
