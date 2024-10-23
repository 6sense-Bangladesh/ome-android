package com.ome.app.data.remote.base

import com.google.gson.Gson
import com.ome.app.domain.model.base.ErrorType
import com.ome.app.domain.model.base.ResponseWrapper
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

open class BaseRepository {

    suspend fun <T> safeApiCall(dispatcher: CoroutineContext, apiCall: suspend () -> T): com.ome.app.domain.model.base.ResponseWrapper<T> {
        return withContext(dispatcher) {
            try {
                com.ome.app.domain.model.base.ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is UnknownHostException -> com.ome.app.domain.model.base.ResponseWrapper.Error(
                        com.ome.app.domain.model.base.ErrorType.NO_INTERNET.message, com.ome.app.domain.model.base.ErrorType.NO_INTERNET)
                    is IOException -> com.ome.app.domain.model.base.ResponseWrapper.Error(com.ome.app.domain.model.base.ErrorType.NETWORK.message, com.ome.app.domain.model.base.ErrorType.NETWORK)
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        com.ome.app.domain.model.base.ResponseWrapper.Error(errorResponse?.message ?: com.ome.app.domain.model.base.ErrorType.DEFAULT.message, code = code)
                    }
                    else -> {
                        com.ome.app.domain.model.base.ResponseWrapper.Error(throwable.message ?: com.ome.app.domain.model.base.ErrorType.DEFAULT.message)
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
