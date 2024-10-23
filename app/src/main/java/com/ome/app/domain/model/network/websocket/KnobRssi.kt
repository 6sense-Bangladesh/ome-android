package com.ome.app.domain.model.network.websocket

data class KnobRssi(
    val macAddr: String,
    val name: String,
    val value: Int
)
