package com.ome.app.data.remote

import com.ome.app.model.network.request.*
import com.ome.app.model.network.response.*
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

    @POST("/knob/newLevel/{macAddress}")
    suspend fun changeKnobAngle(
        @Body params: ChangeKnobAngle,
        @Path("macAddress") macAddress: String
    ): ChangeKnobAngleResponse

    @POST("/knob/initCalibration/{macAddress}")
    suspend fun initCalibration(
        @Body params: InitCalibrationRequest,
        @Path("macAddress") macAddress: String
    ): KnobDto

    @POST("/knob/calibration/{macAddress}")
    suspend fun setCalibration(
        @Body params: SetCalibrationRequest,
        @Path("macAddress") macAddress: String
    ): KnobDto
}
