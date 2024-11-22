package com.ome.app.data.remote.repo

import com.ome.app.data.remote.UserService
import com.ome.app.domain.NetworkCall.respectErrorApiCall
import com.ome.app.domain.NetworkCall.safeApiCall
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.response.BaseResponse
import com.ome.app.domain.model.network.response.UrlToUploadImageResponse
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.RequestBody
import kotlin.coroutines.coroutineContext

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository {

    override val userFlow: MutableStateFlow<UserResponse?> = MutableStateFlow(null)

    override suspend fun getUserData(): ResponseWrapper<UserResponse> {
        return safeApiCall(coroutineContext) {
            val response = userService.getUserInfo()
            userFlow.emit(response)
            response
        }
    }

    override suspend fun getUrlToUploadImage(fileName: String): ResponseWrapper<UrlToUploadImageResponse> {
        return safeApiCall(coroutineContext) {
            userService.getUrlToUploadImage(fileName)
        }
    }

    override suspend fun uploadImage(url: String, requestBody: RequestBody) {
        respectErrorApiCall(coroutineContext) {
            userService.uploadImage(url = url, body = requestBody)
        }
    }

    override suspend fun createUser(params: CreateUserRequest): ResponseWrapper<UserResponse> {
        return safeApiCall(coroutineContext) {
            val response = userService.createUser(params)
            userFlow.emit(response)
            response
        }
    }

    override suspend fun updateUser(params: CreateUserRequest): ResponseWrapper<UserResponse> {
        return safeApiCall(coroutineContext) {
            val response = userService.updateUser(params)
            userFlow.emit(response)
            response
        }
    }

    override suspend fun deleteUser(): BaseResponse = respectErrorApiCall(coroutineContext) {
        userService.deleteUser()
    }

}
