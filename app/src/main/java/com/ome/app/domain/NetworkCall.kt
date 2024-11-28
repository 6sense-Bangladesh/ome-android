package com.ome.app.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ome.app.domain.model.base.ErrorType
import com.ome.app.domain.model.base.ResponseWrapper
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

object NetworkCall {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineContext,
        apiCall: suspend () -> T
    ): ResponseWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is UnknownHostException -> ResponseWrapper.Error(
                        ErrorType.NO_INTERNET.message, ErrorType.NO_INTERNET
                    )

                    is IOException -> ResponseWrapper.Error(
                        ErrorType.NETWORK.message,
                        ErrorType.NETWORK
                    )

                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResponseWrapper.Error(
                            errorResponse ?: ErrorType.DEFAULT.message,
                            code = code
                        )
                    }

                    else -> {
                        ResponseWrapper.Error(throwable.message ?: ErrorType.DEFAULT.message)
                    }
                }
            }
        }
    }

    suspend fun <T> respectErrorApiCall(dispatcher: CoroutineContext, apiCall: suspend () -> T): T {
        return withContext(dispatcher) {
            try {
                apiCall.invoke()
            } catch (throwable: Throwable) {
                when (throwable) {
                    is UnknownHostException -> error(ErrorType.NO_INTERNET.message)
                    is IOException -> throw IOException(ErrorType.NETWORK.message)
                    is HttpException -> {
                        val errorResponse = convertErrorBody(throwable)
                        error(errorResponse?.formattedError() ?: ErrorType.DEFAULT.message)
                    }

                    else -> {
                        if (throwable.message != null)
                            throw throwable
                        else
                            error(ErrorType.DEFAULT.message)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): String? {
        return try {
            val errorBody = throwable.response()?.errorBody()?.string()
            Gson().fromJson(errorBody, ErrorResponse::class.java)?.message.let {
                if(it.isNullOrEmpty())
                    Gson().fromJson(errorBody, ErrorResponseType2::class.java)?.message
                else it
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    private fun String.formattedError(): String {
        return this.trim()
            .replace(Regex("(?<!^)([A-Z])"), " $1")
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .let { formatted ->
                if (formatted.endsWith(".")) formatted else "$formatted."
            }
    }

    data class ErrorResponse(@SerializedName("message") val message: String? = "")
    data class ErrorResponseType2(@SerializedName("Message") val message: String? = "")
}
