package com.ome.app.domain.model.network.request


import com.google.gson.annotations.SerializedName

data class ScheduleRequest(
    @SerializedName("knobRotationPlan")
    val knobRotationPlan: List<KnobRotationPlan> = listOf()
) {
    data class KnobRotationPlan(
        @SerializedName("targetAngle")
        val targetAngle: Int = 0,
        @SerializedName("timeAtCurrentPosition")
        val timeAtCurrentPosition: Int = 0
    )
}