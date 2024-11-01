package com.ome.app.data.remote

import com.ome.app.domain.model.network.request.*
import com.ome.app.domain.model.network.response.*
import retrofit2.http.*

interface StoveService {
    @POST("stove")
    suspend fun createStove(@Body params: StoveRequest): StoveResponse

    @GET("knobs")
    suspend fun getAllKnobs(): List<KnobDto>

    @PATCH("stove/{stoveId}")
    suspend fun updateStoveInfo(
        @Body params: StoveRequest,
        @Path("stoveId") stoveId: String
    ): StoveResponse

    @GET("/knob/ownership/{macAddress}")
    suspend fun getKnobOwnership(
        @Path("macAddress") macAddress: String
    ): KnobOwnershipResponse

    @POST("/knob/{macAddress}")
    suspend fun createKnob(
        @Body params: CreateKnobRequest,
        @Path("macAddress") macAddress: String
    ): CreateKnobResponse

    @DELETE("/knob/{macAddress}")
    suspend fun deleteKnob(
        @Path("macAddress") macAddress: String
    ): BaseResponse

    @PATCH("/knob/{macAddress}")
    suspend fun updateKnobInfo(
        @Body params: CreateKnobRequest,
        @Path("macAddress") macAddress: String
    ): CreateKnobResponse

    @POST("/knob/clearWifi/{macAddress}")
    suspend fun clearWifi(
        @Path("macAddress") macAddress: String
    ): BaseResponse

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
