package com.ome.app.domain.model.network.request

data class StoveRequest(
    var stoveAutoOffMins: Int? = null,
    var stoveGasOrElectric: String? = null,
    var stoveMakeModel: String? = null,
    var stoveOrientation: Int? = null,
    var stoveKnobMounting: String? = null,
    var stoveSetupComplete: Boolean? = null
)
