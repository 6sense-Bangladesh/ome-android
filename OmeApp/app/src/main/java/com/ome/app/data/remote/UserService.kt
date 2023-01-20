package com.ome.app.data.remote

import com.ome.app.model.network.UserResponse
import retrofit2.http.GET


interface UserService {
    @GET("user")
    suspend fun getUserInfo(): UserResponse
}
