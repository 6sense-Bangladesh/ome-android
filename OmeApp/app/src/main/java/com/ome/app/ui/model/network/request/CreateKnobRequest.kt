package com.ome.app.ui.model.network.request

data class CreateKnobRequest(
    val highAngle: Int? = null,
    val lowAngle: Int? = null,
    val mediumAngle: Int? = null,
    val offAngle: Int? = null,
    val macID: String? = null,
    val stovePosition: Int? = null
)
