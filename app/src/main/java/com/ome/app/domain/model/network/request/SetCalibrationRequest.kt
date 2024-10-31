package com.ome.app.domain.model.network.request

data class SetCalibrationRequest(
    val offAngle: Int? = null,
    val rotationDir: Int,
    val zones: List<Zone> = emptyList()
)
