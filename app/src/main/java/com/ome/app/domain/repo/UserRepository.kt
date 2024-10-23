package com.ome.app.domain.repo

import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.response.BaseResponse
import com.ome.app.domain.model.network.response.UrlToUploadImageResponse
import com.ome.app.domain.model.network.response.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.RequestBody

interface UserRepository {
    suspend fun getUserData(): ResponseWrapper<UserResponse>
    suspend fun getUrlToUploadImage(fileName: String): ResponseWrapper<UrlToUploadImageResponse>
    suspend fun uploadImage(url: String, requestBody: RequestBody)
    suspend fun createUser(params: CreateUserRequest): ResponseWrapper<UserResponse>
    suspend fun updateUser(params: CreateUserRequest): ResponseWrapper<UserResponse>
    suspend fun deleteUser(): BaseResponse
    val userFlow: MutableStateFlow<UserResponse?>
}