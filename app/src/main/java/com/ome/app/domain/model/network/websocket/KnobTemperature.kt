package com.ome.app.domain.model.network.websocket

data class KnobTemperature(
    val macAddr: String,
    val name: String,
    val value: Double
)
