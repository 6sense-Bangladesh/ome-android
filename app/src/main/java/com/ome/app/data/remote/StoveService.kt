package com.ome.app.data.remote

import com.ome.app.model.network.request.*
import com.ome.app.ui.model.network.request.CreateKnobRequest
import com.ome.app.ui.model.network.request.CreateStoveRequest
import com.ome.app.ui.model.network.response.BaseResponse
import com.ome.app.ui.model.network.response.ChangeKnobAngleResponse
import com.ome.app.ui.model.network.response.CreateKnobResponse
import com.ome.app.ui.model.network.response.CreateStoveResponse
import com.ome.app.ui.model.network.response.KnobDto
import com.ome.app.ui.model.network.response.KnobOwnershipResponse
import retrofit2.http.*

interface StoveService {
    @POST("stove")
    suspend fun createStove(@Body params: CreateStoveRequest): CreateStoveResponse

    @GET("knobs")
    suspend fun getAllKnobs(): List<KnobDto>

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
