package com.ome.app.model.network.websocket

import com.google.gson.annotations.SerializedName

class KnobMessageEvent(
    @SerializedName("macAddr")
    val macAddr: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("value")
    val value: Any
)
