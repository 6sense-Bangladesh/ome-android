package com.ome.app.data.remote.repo

import com.ome.app.data.remote.StoveService
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.NetworkCall.safeApiCall
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.*
import com.ome.app.domain.model.network.response.*
import com.ome.app.domain.repo.StoveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.coroutineContext

class StoveRepositoryImpl(
    private val stoveService: StoveService,
    private val webSocketManager: WebSocketManager
) : StoveRepository {

    override suspend fun createStove(params: StoveRequest): ResponseWrapper<StoveResponse> {
        return safeApiCall(coroutineContext) {
            stoveService.createStove(params)
        }
    }

    override suspend fun updateStove(
        params: StoveRequest,
        stoveId: String
    ): ResponseWrapper<StoveResponse> {
        return safeApiCall(coroutineContext) {
            stoveService.updateStoveInfo(params, stoveId)
        }
    }

    override suspend fun getAllKnobs(): List<KnobDto> {
        val response = safeApiCall(coroutineContext) {
            stoveService.getAllKnobs().also {
                knobsFlow.value = it
            }
        }.getOrNull().orEmpty()
        knobsFlow.value = response
        return response
    }

    override suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse {
        return stoveService.getKnobOwnership(macAddress)
    }

    override suspend fun initCalibration(
        params: InitCalibrationRequest,
        macAddress: String
    ): KnobDto {
        return stoveService.initCalibration(params, macAddress)
    }

    override suspend fun setCalibration(
        params: SetCalibrationRequest,
        macAddress: String
    ): KnobDto {
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

    override suspend fun deleteKnob(macAddress: String): BaseResponse {
        stoveService.updateKnobInfo(CreateKnobRequest(calibrated = false), macAddress)
        return stoveService.deleteKnob(macAddress)
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

    override val knobsFlow: MutableStateFlow<List<KnobDto>> = MutableStateFlow(listOf())

}
