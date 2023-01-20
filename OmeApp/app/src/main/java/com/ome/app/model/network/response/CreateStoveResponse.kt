package com.ome.app.model.network.response

data class CreateStoveResponse(
    val knobMacAddrs: List<String>,
    val numKnobs: Int,
    val stoveAutoOffMins: Int,
    val stoveId: String,
    val stoveMakeModel: String,
    val stoveOrientation: Int,
    val stoveSetupComplete: Any
)
