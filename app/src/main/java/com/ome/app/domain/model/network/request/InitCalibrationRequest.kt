package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class InitCalibrationRequest(
    @SerializedName("calibrationId")
    val offAngle: Int,
    @SerializedName("rotationDir")
    val rotationDir: Int)
