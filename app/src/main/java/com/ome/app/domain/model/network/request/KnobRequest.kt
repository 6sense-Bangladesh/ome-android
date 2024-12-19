package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class KnobRequest(
    @SerializedName("stovePosition")
    val stovePosition: Int? = null,
    @SerializedName("calibrated")
    val calibrated: Boolean? = null
)
