package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class ChangeKnobAngleResponse(
    @SerializedName("level")
    val level: Int?
)
