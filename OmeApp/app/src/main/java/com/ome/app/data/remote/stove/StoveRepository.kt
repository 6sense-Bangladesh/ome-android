package com.ome.app.data.remote.stove

import com.ome.app.data.remote.StoveService
import com.ome.app.data.remote.base.BaseRepository
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.model.network.request.CreateKnobRequest
import com.ome.app.model.network.request.CreateStoveRequest
import com.ome.app.model.network.response.CreateKnobResponse
import com.ome.app.model.network.response.CreateStoveResponse
import com.ome.app.model.network.response.KnobDto
import com.ome.app.model.network.response.KnobOwnershipResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.coroutineContext

interface StoveRepository {
    suspend fun createStove(params: CreateStoveRequest): ResponseWrapper<CreateStoveResponse>
    suspend fun updateStove(params: CreateStoveRequest, stoveId: String): CreateStoveResponse
    suspend fun getAllKnobs(): List<KnobDto>
    suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse
    suspend fun createKnob(params: CreateKnobRequest, macAddress: String): CreateKnobResponse
    val knobsFlow: MutableStateFlow<List<KnobDto>?>
}

class StoveRepositoryImpl(
    private val stoveService: StoveService
) : StoveRepository, BaseRepository() {

    override suspend fun createStove(params: CreateStoveRequest): ResponseWrapper<CreateStoveResponse> {
        return safeApiCall(coroutineContext) {
            stoveService.createStove(params)
        }
    }

    override suspend fun updateStove(
        params: CreateStoveRequest,
        stoveId: String
    ): CreateStoveResponse {
        return stoveService.updateStoveInfo(params, stoveId)
    }

    override suspend fun getAllKnobs(): List<KnobDto> {
        val response = stoveService.getAllKnobsResponse()
        knobsFlow.tryEmit(response)
        return response
    }

    override suspend fun getKnobOwnership(macAddress: String): KnobOwnershipResponse {
        return stoveService.getKnobOwnership(macAddress)
    }

    override suspend fun createKnob(
        params: CreateKnobRequest,
        macAddress: String
    ): CreateKnobResponse {
        return stoveService.createKnob(params, macAddress)
    }

    override val knobsFlow: MutableStateFlow<List<KnobDto>?> = MutableStateFlow(listOf())

}
