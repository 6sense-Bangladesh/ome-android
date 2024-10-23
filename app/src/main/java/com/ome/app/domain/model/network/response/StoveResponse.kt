package com.ome.app.domain.model.network.response

data class StoveResponse(
    val knobMacAddrs: List<String>,
    val numKnobs: Int,
    val stoveAutoOffMins: Int,
    val stoveId: String,
    val stoveMakeModel: String,
    val stoveOrientation: Int,
    val stoveSetupComplete: Boolean?
)
