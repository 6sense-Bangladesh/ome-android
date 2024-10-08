package com.ome.app.model.network.websocket

data class KnobTemperature(
    val macAddr: String,
    val name: String,
    val value: Double
)
