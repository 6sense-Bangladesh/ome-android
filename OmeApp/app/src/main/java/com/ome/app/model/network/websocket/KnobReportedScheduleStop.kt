package com.ome.app.model.network.websocket

data class KnobReportedScheduleStop(
    val macAddr: String,
    val name: String,
    val value: Int
)
