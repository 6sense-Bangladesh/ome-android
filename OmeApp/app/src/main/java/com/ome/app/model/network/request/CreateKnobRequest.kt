package com.ome.app.model.network.request

data class CreateKnobRequest(
    val highAngle: Int,
    val lowAngle: Int,
    val mediumAngle: Int,
    val offAngle: Int,
    val macID: String,
    val stovePosition: Int
)
