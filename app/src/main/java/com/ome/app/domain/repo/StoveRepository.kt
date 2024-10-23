package com.ome.app.domain.repo

import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.*
import com.ome.app.domain.model.network.response.*
import kotlinx.coroutines.flow.MutableStateFlow

interface StoveRepository {
    suspend fun createStove(params: StoveRequest): ResponseWrapper<StoveResponse>
    suspend fun updateStove(params: StoveRequest, stoveId: String): ResponseWrapper<StoveResponse>
    suspend fun getAllKnobs(): List<KnobDto>
    suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse
    suspend fun initCalibration(params: InitCalibrationRequest, macAddress: String): KnobDto
    suspend fun setCalibration(params: SetCalibrationRequest, macAddress: String): KnobDto
    suspend fun changeKnobAngle(params: ChangeKnobAngle, macAddress: String): ChangeKnobAngleResponse
    suspend fun createKnob(params: CreateKnobRequest, macAddress: String): CreateKnobResponse
    suspend fun updateKnobInfo(params: CreateKnobRequest, macAddress: String): CreateKnobResponse
    suspend fun clearWifi(macAddress: String): BaseResponse
    val knobsFlow: MutableStateFlow<List<KnobDto>?>
}