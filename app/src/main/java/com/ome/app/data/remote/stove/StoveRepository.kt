package com.ome.app.data.remote.stove

import com.ome.app.data.remote.StoveService
import com.ome.app.data.remote.base.BaseRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.model.network.request.ChangeKnobAngle
import com.ome.app.model.network.request.InitCalibrationRequest
import com.ome.app.model.network.request.SetCalibrationRequest
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.ui.model.network.request.CreateKnobRequest
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.ui.model.network.response.BaseResponse
import com.ome.app.ui.model.network.response.ChangeKnobAngleResponse
import com.ome.app.ui.model.network.response.CreateKnobResponse
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.ui.model.network.response.KnobOwnershipResponse
import com.ome.app.ui.model.network.response.StoveResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.coroutineContext

interface StoveRepository {
    suspend fun createStove(params: com.ome.app.domain.model.network.request.StoveRequest): com.ome.app.domain.model.base.ResponseWrapper<StoveResponse>
    suspend fun updateStove(params: com.ome.app.domain.model.network.request.StoveRequest, stoveId: String): com.ome.app.domain.model.base.ResponseWrapper<StoveResponse>
    suspend fun getAllKnobs(): List<com.ome.app.domain.model.network.response.KnobDto>
    suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse
    suspend fun initCalibration(params: InitCalibrationRequest, macAddress: String): com.ome.app.domain.model.network.response.KnobDto
    suspend fun setCalibration(params: SetCalibrationRequest, macAddress: String): com.ome.app.domain.model.network.response.KnobDto
    suspend fun changeKnobAngle(params: ChangeKnobAngle, macAddress: String): ChangeKnobAngleResponse
    suspend fun createKnob(params: CreateKnobRequest, macAddress: String): CreateKnobResponse
    suspend fun updateKnobInfo(params: CreateKnobRequest, macAddress: String): CreateKnobResponse
    suspend fun clearWifi(macAddress: String): BaseResponse
    val knobsFlow: MutableStateFlow<List<com.ome.app.domain.model.network.response.KnobDto>?>
}

class StoveRepositoryImpl(
    private val stoveService: StoveService,
    private val webSocketManager: WebSocketManager
) : StoveRepository, BaseRepository() {

    override suspend fun createStove(params: com.ome.app.domain.model.network.request.StoveRequest): com.ome.app.domain.model.base.ResponseWrapper<StoveResponse> {
        return safeApiCall(coroutineContext) {
            stoveService.createStove(params)
        }
    }

    override suspend fun updateStove(
        params: com.ome.app.domain.model.network.request.StoveRequest,
        stoveId: String
    ): com.ome.app.domain.model.base.ResponseWrapper<StoveResponse> {
        return safeApiCall(coroutineContext) {
            stoveService.updateStoveInfo(params, stoveId)
        }
    }

    override suspend fun getAllKnobs(): List<com.ome.app.domain.model.network.response.KnobDto> {
        val response = stoveService.getAllKnobs()
        knobsFlow.tryEmit(response)
        return response
    }

    override suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse {
        return stoveService.getKnobOwnership(macAddress)
    }

    override suspend fun initCalibration(
        params: InitCalibrationRequest,
        macAddress: String
    ): com.ome.app.domain.model.network.response.KnobDto {
        return stoveService.initCalibration(params, macAddress)
    }

    override suspend fun setCalibration(
        params: SetCalibrationRequest,
        macAddress: String
    ): com.ome.app.domain.model.network.response.KnobDto {
        return stoveService.setCalibration(params, macAddress)
    }

    override suspend fun changeKnobAngle(
        params: ChangeKnobAngle,
        macAddress: String
    ): ChangeKnobAngleResponse {
        return stoveService.changeKnobAngle(params,macAddress)
    }

    override suspend fun createKnob(
        params: CreateKnobRequest,
        macAddress: String
    ): CreateKnobResponse {
        return stoveService.createKnob(params, macAddress)
    }

    override suspend fun updateKnobInfo(
        params: CreateKnobRequest,
        macAddress: String
    ): CreateKnobResponse {
        return stoveService.updateKnobInfo(params, macAddress)
    }

    override suspend fun clearWifi(macAddress: String): BaseResponse {
        return stoveService.clearWifi(macAddress)
    }

    override val knobsFlow: MutableStateFlow<List<com.ome.app.domain.model.network.response.KnobDto>?> = MutableStateFlow(listOf())

}
