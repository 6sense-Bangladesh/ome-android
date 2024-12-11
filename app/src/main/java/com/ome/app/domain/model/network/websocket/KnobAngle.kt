package com.ome.app.domain.model.network.websocket

import com.google.gson.annotations.SerializedName

data class KnobAngle(
    @SerializedName("macAddr") val macAddr: String,
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: Double
)
