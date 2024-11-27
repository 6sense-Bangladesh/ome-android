package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class KnobRequest(
    @SerializedName("stove_position")
    val stovePosition: Int? = null,
    @SerializedName("calibrated")
    val calibrated: Boolean? = null,
    @SerializedName("safetyLock")
    val safetyLock: Boolean? = null,
)
