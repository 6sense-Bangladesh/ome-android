package com.ome.app.data.remote.stove

import com.ome.app.data.remote.StoveService
import com.ome.app.data.remote.base.BaseRepository
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.model.network.request.CreateStoveRequest
import com.ome.app.model.network.response.CreateStoveResponse
import kotlin.coroutines.coroutineContext

interface StoveRepository {
    suspend fun createStove(params: CreateStoveRequest): ResponseWrapper<CreateStoveResponse>
    suspend fun updateStove(params: CreateStoveRequest, stoveId: String): CreateStoveResponse
}

class StoveRepositoryImpl(
    private val userService: StoveService
) : StoveRepository, BaseRepository() {

    override suspend fun createStove(params: CreateStoveRequest): ResponseWrapper<CreateStoveResponse> {
        return safeApiCall(coroutineContext) {
            userService.createStove(params)
        }
    }

    override suspend fun updateStove(params: CreateStoveRequest, stoveId: String): CreateStoveResponse {
        return userService.updateStoveInfo(params, stoveId)
    }

}
