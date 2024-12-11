package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class SetCalibrationRequest(
    @SerializedName("offAngle")
    val offAngle: Int? = null,
    @SerializedName("rotationDir")
    val rotationDir: Int,
    @SerializedName("zones")
    val zones: List<Zone> = emptyList()
)
