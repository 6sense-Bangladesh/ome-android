package com.ome.app.data.remote

import com.ome.app.model.network.request.ChangeKnobAngle
import com.ome.app.model.network.request.InitCalibrationRequest
import com.ome.app.model.network.request.SetCalibrationRequest
import com.ome.app.ui.model.network.request.CreateKnobRequest
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.ui.model.network.response.BaseResponse
import com.ome.app.ui.model.network.response.ChangeKnobAngleResponse
import com.ome.app.ui.model.network.response.CreateKnobResponse
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.ui.model.network.response.KnobOwnershipResponse
import com.ome.app.ui.model.network.response.StoveResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StoveService {
    @POST("stove")
    suspend fun createStove(@Body params: com.ome.app.domain.model.network.request.StoveRequest): StoveResponse

    @GET("knobs")
    suspend fun getAllKnobs(): List<com.ome.app.domain.model.network.response.KnobDto>

    @PATCH("stove/{stoveId}")
    suspend fun updateStoveInfo(
        @Body params: com.ome.app.domain.model.network.request.StoveRequest,
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
    ): com.ome.app.domain.model.network.response.KnobDto

    @POST("/knob/calibration/{macAddress}")
    suspend fun setCalibration(
        @Body params: SetCalibrationRequest,
        @Path("macAddress") macAddress: String
    ): com.ome.app.domain.model.network.response.KnobDto
}
