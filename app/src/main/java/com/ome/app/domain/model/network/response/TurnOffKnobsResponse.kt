package com.ome.app.domain.model.network.response


import com.google.gson.annotations.SerializedName

data class TurnOffKnobsResponse(
    @SerializedName("knobsTurned")
    val knobsTurned: List<KnobsTurned> = listOf()
) {
    data class KnobsTurned(
        @SerializedName("macAddr")
        val macAddr: String = "",
        @SerializedName("offAngle")
        val offAngle: Int = 0
    )
}