package com.ome.app.data.remote

import com.ome.app.model.network.request.CreateUserRequest
import com.ome.app.model.network.response.DeleteUserResponse
import com.ome.app.model.network.response.UrlToUploadImageResponse
import com.ome.app.model.network.response.UserResponse
import okhttp3.RequestBody
import retrofit2.http.*


interface UserService {
    @GET("user")
    suspend fun getUserInfo(): UserResponse

    @POST("createUser")
    suspend fun createUser(@Body params: CreateUserRequest): UserResponse

    @PATCH("updateUser")
    suspend fun updateUser(@Body params: CreateUserRequest): UserResponse

    @GET("/user/uploadImageUrl")
    suspend fun getUrlToUploadImage(@Query("fileName") fileName: String): UrlToUploadImageResponse

    @PUT
    suspend fun uploadImage(@Url url: String, @Body body: RequestBody)

    @DELETE("user")
    suspend fun deleteUser(): DeleteUserResponse
}
