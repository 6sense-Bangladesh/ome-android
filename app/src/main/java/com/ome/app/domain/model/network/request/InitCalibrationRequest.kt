package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class InitCalibrationRequest(
    @SerializedName("offAngle")
    val offAngle: Int,
    @SerializedName("rotationDir")
    val rotationDir: Int)
