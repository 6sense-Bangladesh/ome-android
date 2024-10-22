package com.ome.app.data.remote.base

import com.google.gson.Gson
import com.ome.app.ui.model.base.ErrorType
import com.ome.app.ui.model.base.ResponseWrapper
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

open class BaseRepository {

    suspend fun <T> safeApiCall(dispatcher: CoroutineContext, apiCall: suspend () -> T): ResponseWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is UnknownHostException -> ResponseWrapper.Error(ErrorType.NO_INTERNET.message, ErrorType.NO_INTERNET)
                    is IOException -> ResponseWrapper.Error(ErrorType.NETWORK.message, ErrorType.NETWORK)
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResponseWrapper.Error(errorResponse?.message ?: ErrorType.DEFAULT.message, code = code)
                    }
                    else -> {
                        ResponseWrapper.Error(throwable.message ?: ErrorType.DEFAULT.message)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            Gson().fromJson(throwable.response()?.errorBody()?.string(), ErrorResponse::class.java)
        } catch (exception: Exception) {
            null
        }
    }
}
