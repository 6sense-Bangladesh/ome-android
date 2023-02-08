package com.ome.app.data.remote

import com.ome.app.model.network.request.CreateKnobRequest
import com.ome.app.model.network.request.CreateStoveRequest
import com.ome.app.model.network.response.CreateKnobResponse
import com.ome.app.model.network.response.CreateStoveResponse
import com.ome.app.model.network.response.KnobDto
import com.ome.app.model.network.response.KnobOwnershipResponse
import retrofit2.http.*

interface StoveService {
    @POST("stove")
    suspend fun createStove(@Body params: CreateStoveRequest): CreateStoveResponse

    @GET("knobs")
    suspend fun getAllKnobsResponse(): List<KnobDto>

    @PATCH("stove/{stoveId}")
    suspend fun updateStoveInfo(
        @Body params: CreateStoveRequest,
        @Path("stoveId") stoveId: String
    ): CreateStoveResponse

    @GET("/knob/ownership/{macAddress}")
    suspend fun getKnobOwnership(
        @Path("macAddress") macAddress: String
    ): KnobOwnershipResponse

    @POST("/knob/{macAddress}")
    suspend fun createKnob(
        @Body params: CreateKnobRequest,
        @Path("macAddress") macAddress: String
    ): CreateKnobResponse
}
