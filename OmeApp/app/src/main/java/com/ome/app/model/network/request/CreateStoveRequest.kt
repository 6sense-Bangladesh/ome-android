package com.ome.app.model.network.request

data class CreateStoveRequest(
    val stoveAutoOffMins: Int,
    val stoveGasOrElectric: String,
    val stoveMakeModel: String,
    val stoveOrientation: Int
)
