package com.ome.app.data.remote.repo

import com.ome.app.data.remote.StoveService
import com.ome.app.domain.NetworkCall.respectErrorApiCall
import com.ome.app.domain.NetworkCall.safeApiCall
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.*
import com.ome.app.domain.model.network.response.*
import com.ome.app.domain.repo.StoveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.coroutineContext

class StoveRepositoryImpl(
    private val stoveService: StoveService
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

    override suspend fun turnOffAllKnobs(): TurnOffKnobsResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.turnOffAllKnobs()
        }
    }

    override suspend fun getAllKnobs(): List<KnobDto> {
        val response = safeApiCall(coroutineContext) {
            stoveService.getAllKnobs()
        }.getOrNull().orEmpty()
//            .let {
//                it.toMutableList().apply {
//                    if (BuildConfig.IS_INTERNAL_TESTING)
//                        addAll(dummyKnobs)
//                }
//            }
        knobsFlow.value = response
        return response
    }

    override suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.getKnobOwnership(macAddress)
        }
    }

    override suspend fun initCalibration(
        params: InitCalibrationRequest,
        macAddress: String
    ): KnobDto {
        return respectErrorApiCall(coroutineContext) {
            stoveService.initCalibration(params, macAddress)
        }
    }

    override suspend fun setCalibration(
        params: SetCalibrationRequest,
        macAddress: String
    ): KnobDto {
        return respectErrorApiCall(coroutineContext) {
            stoveService.setCalibration(params, macAddress)
        }
    }

    override suspend fun changeKnobAngle(
        params: ChangeKnobAngle,
        macAddress: String
    ): ChangeKnobAngleResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.changeKnobAngle(params, macAddress)
        }
    }

    override suspend fun createKnob(
        params: KnobRequest,
        macAddress: String
    ): CreateKnobResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.createKnob(params, macAddress)
        }
    }

    override suspend fun deleteKnob(macAddress: String): BaseResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.deleteKnob(macAddress)
        }
    }

    override suspend fun updateKnobInfo(
        params: KnobRequest,
        macAddress: String
    ): CreateKnobResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.updateKnobInfo(params, macAddress)
        }
    }

    override suspend fun clearWifi(macAddress: String): BaseResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.clearWifi(macAddress)
        }
    }

    override suspend fun setSafetyLockOn(vararg macAddress: String) {
        respectErrorApiCall(coroutineContext) {
            macAddress.forEach {
                stoveService.setSafetyLockOn(it)
            }
        }
    }

    override suspend fun setSafetyLockOff(vararg macAddress: String) {
        respectErrorApiCall(coroutineContext) {
            macAddress.forEach {
                stoveService.setSafetyLockOff(it)
            }
        }
    }

    override suspend fun startTurnOffTimer(
        macAddress: String,
        currentAngle: Int,
        offAngle: Int,
        second: Int
    ): BaseResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.startSchedule(
                params = ScheduleRequest(listOf(
                    ScheduleRequest.KnobRotationPlan(currentAngle, 0),
                    ScheduleRequest.KnobRotationPlan(offAngle, second)
                )),
                macAddress = macAddress
            )
        }
    }

    override suspend fun stopTimer(macAddress: String): BaseResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.stopSchedule(macAddress)
        }
    }

    override suspend fun pauseTimer(macAddress: String): BaseResponse {
        return respectErrorApiCall(coroutineContext) {
            stoveService.pauseSchedule(macAddress)
        }
    }

    override val knobsFlow: MutableStateFlow<List<KnobDto>> = MutableStateFlow(listOf())

}
