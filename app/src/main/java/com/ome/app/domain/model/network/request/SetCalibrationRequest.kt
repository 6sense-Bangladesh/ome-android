package com.ome.app.domain.model.network.request

data class SetCalibrationRequest(
    val offAngle: Int,
    val rotationDir: Int,
    val zones: List<Zone>
)
