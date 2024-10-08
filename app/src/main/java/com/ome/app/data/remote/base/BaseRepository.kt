package com.ome.app.data.remote.base

import com.google.gson.Gson
import com.ome.app.model.base.ResponseWrapper
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.CoroutineContext

open class BaseRepository {

    suspend fun <T> safeApiCall(dispatcher: CoroutineContext, apiCall: suspend () -> T): ResponseWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResponseWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResponseWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResponseWrapper.GenericError(null, ErrorResponse(throwable.message))
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
