package com.ome.app.model.network.request

data class CreateStoveRequest(
    val stoveAutoOffMins: Int? = null,
    val stoveGasOrElectric: String? = null,
    val stoveMakeModel: String? = null,
    val stoveOrientation: Int? = null
)
