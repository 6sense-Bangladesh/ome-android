package com.ome.app.data.remote.user

import com.ome.app.data.remote.UserService
import com.ome.app.data.remote.base.BaseRepository
import com.ome.app.ui.model.base.ResponseWrapper
import com.ome.app.model.network.response.UrlToUploadImageResponse
import com.ome.app.ui.model.network.request.CreateUserRequest
import com.ome.app.model.network.response.DeleteUserResponse
import com.ome.app.ui.model.network.response.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.RequestBody
import kotlin.coroutines.coroutineContext

interface UserRepository {
    suspend fun getUserData(): ResponseWrapper<UserResponse>
    suspend fun getUrlToUploadImage(fileName: String): ResponseWrapper<UrlToUploadImageResponse>
    suspend fun uploadImage(url: String, requestBody: RequestBody)
    suspend fun createUser(params: CreateUserRequest): ResponseWrapper<UserResponse>
    suspend fun updateUser(params: CreateUserRequest): ResponseWrapper<UserResponse>
    suspend fun deleteUser(): DeleteUserResponse
    val userFlow: MutableStateFlow<UserResponse?>
}

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository, BaseRepository() {

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
        userService.uploadImage(url = url, body = requestBody)
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

    override suspend fun deleteUser(): DeleteUserResponse = userService.deleteUser()

}
