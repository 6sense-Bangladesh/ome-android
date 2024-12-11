package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class StoveRequest(
    @SerializedName("stoveAutoOffMins")
    var stoveAutoOffMins: Int? = null,
    @SerializedName("stoveGasOrElectric")
    var stoveGasOrElectric: String? = null,
    @SerializedName("stoveMakeModel")
    var stoveMakeModel: String? = null,
    @SerializedName("stoveOrientation")
    var stoveOrientation: Int? = null,
    @SerializedName("stoveKnobMounting")
    var stoveKnobMounting: String? = null,
    @SerializedName("stoveSetupComplete")
    var stoveSetupComplete: Boolean? = null
)
