package com.ome.app.data.remote

import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.response.BaseResponse
import com.ome.app.domain.model.network.response.UserResponse
import okhttp3.RequestBody
import retrofit2.http.*


interface UserService {
    @GET("user")
    suspend fun getUserInfo(): UserResponse

    @POST("user")
    suspend fun createUser(@Body params: CreateUserRequest): UserResponse

    @PATCH("user")
    suspend fun updateUser(@Body params: CreateUserRequest): UserResponse

    @GET("/user/uploadImageUrl")
    suspend fun getUrlToUploadImage(@Query("fileName") fileName: String): com.ome.app.domain.model.network.response.UrlToUploadImageResponse

    @PUT
    suspend fun uploadImage(@Url url: String, @Body body: RequestBody)

    @DELETE("user")
    suspend fun deleteUser(): BaseResponse
}
