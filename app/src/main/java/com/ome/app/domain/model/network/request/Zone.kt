package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class Zone(
    @SerializedName("highAngle") val highAngle: Int,
    @SerializedName("lowAngle") val lowAngle: Int,
    @SerializedName("mediumAngle") val mediumAngle: Int,
    @SerializedName("zoneName") val zoneName: String,
    @SerializedName("zoneNumber") val zoneNumber: Int
)
