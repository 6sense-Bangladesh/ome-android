package com.ome.app.model.network.response

data class CalibrationDto(
    val offAngle: Int,
    val rotationDir: Int,
    val zones: List<ZoneDto>
)
