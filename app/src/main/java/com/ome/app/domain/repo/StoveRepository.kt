package com.ome.app.domain.repo

import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.*
import com.ome.app.domain.model.network.response.*
import kotlinx.coroutines.flow.MutableStateFlow

interface StoveRepository {
    suspend fun createStove(params: StoveRequest): ResponseWrapper<StoveResponse>
    suspend fun updateStove(params: StoveRequest, stoveId: String): ResponseWrapper<StoveResponse>
    suspend fun turnOffAllKnobs(): TurnOffKnobsResponse
    suspend fun getAllKnobs(): List<KnobDto>
    suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse
    suspend fun initCalibration(params: InitCalibrationRequest, macAddress: String): KnobDto
    suspend fun setCalibration(params: SetCalibrationRequest, macAddress: String): KnobDto
    suspend fun changeKnobAngle(params: ChangeKnobAngle, macAddress: String): ChangeKnobAngleResponse
    suspend fun createKnob(params: KnobRequest, macAddress: String): CreateKnobResponse
    suspend fun deleteKnob(macAddress: String): BaseResponse
    suspend fun updateKnobInfo(params: KnobRequest, macAddress: String): CreateKnobResponse
    suspend fun clearWifi(macAddress: String): BaseResponse
    suspend fun setSafetyLockOn(macAddress: List<String>)
    suspend fun setSafetyLockOff(macAddress: List<String>)
    suspend fun startTurnOffTimer(macAddress: String, currentAngle: Int, offAngle: Int, second: Int): BaseResponse
    suspend fun stopTimer(macAddress: String): BaseResponse
    suspend fun pauseTimer(macAddress: String): BaseResponse

    val knobsFlow: MutableStateFlow<List<KnobDto>>
}