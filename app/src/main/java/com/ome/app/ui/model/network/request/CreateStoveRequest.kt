package com.ome.app.ui.model.network.request

data class CreateStoveRequest(
    var stoveAutoOffMins: Int? = null,
    var stoveGasOrElectric: String? = null,
    var stoveMakeModel: String? = null,
    var stoveOrientation: Int? = null,
    var stoveKnobMounting: String? = null,
    var stoveSetupComplete: Boolean? = null
)
