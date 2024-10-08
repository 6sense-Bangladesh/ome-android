package com.ome.app.ui.model.network.response

data class CalibrationDto(
    val offAngle: Int,
    val rotationDir: Int,
    val zones: List<ZoneDto>
)
