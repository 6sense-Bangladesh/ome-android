package com.ome.app.data.remote

import com.ome.app.model.network.request.CreateStoveRequest
import com.ome.app.model.network.response.CreateStoveResponse
import retrofit2.http.*

interface StoveService {
    @POST("stove")
    suspend fun createStove(@Body params: CreateStoveRequest): CreateStoveResponse

    @PATCH("stove/{stoveId}")
    suspend fun updateStoveInfo(
        @Body params: CreateStoveRequest,
        @Path("stoveId") stoveId: String
    ): CreateStoveResponse
}
