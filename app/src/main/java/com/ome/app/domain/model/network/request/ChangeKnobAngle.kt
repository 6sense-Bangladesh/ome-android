package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class ChangeKnobAngle(
    @SerializedName("level") val level: Int
)
