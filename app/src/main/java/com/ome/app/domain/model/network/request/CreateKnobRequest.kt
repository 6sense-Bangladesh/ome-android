package com.ome.app.domain.model.network.request

data class CreateKnobRequest(
    val stovePosition: Int? = null,
    val calibrated: Boolean? = null,
)
