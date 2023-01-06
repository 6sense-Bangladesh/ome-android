package com.ome.app.data.remote.user

import com.ome.app.data.remote.UserService
import com.ome.app.data.remote.base.BaseRepository
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.model.network.UserResponse
import kotlin.coroutines.coroutineContext

interface UserRepository {
    suspend fun getUserData(): ResponseWrapper<UserResponse>
}

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository, BaseRepository() {

    override suspend fun getUserData(): ResponseWrapper<UserResponse> {
        return safeApiCall(coroutineContext) {
            userService.getUserInfo()
        }
    }
}
