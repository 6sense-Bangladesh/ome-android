package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class StoveResponse(
    @SerializedName("knobMacAddrs") val knobMacAddrs: List<String>,
    @SerializedName("numKnobs") val numKnobs: Int,
    @SerializedName("stoveAutoOffMins") val stoveAutoOffMins: Int,
    @SerializedName("stoveId") val stoveId: String,
    @SerializedName("stoveMakeModel") val stoveMakeModel: String,
    @SerializedName("stoveOrientation") val stoveOrientation: Int,
    @SerializedName("stoveSetupComplete") val stoveSetupComplete: Boolean?
)

