package com.ome.app.domain.model.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @SerializedName("userId") val userId: String = "",
    @SerializedName("stoveId") val stoveId: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("numKnobs") val numKnobs: Int = 0,
    @SerializedName("deviceTokens") val deviceTokens: List<String> = listOf(),
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("knobMacAddrs") val knobMacAddrs: List<String> = listOf(),
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("middleName") val middleName: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("stoveAutoOffMins") val stoveAutoOffMins: Int? = null,
    @SerializedName("stoveGasOrElectric") val stoveGasOrElectric: String? = null,
    @SerializedName("stoveKnobMounting") val stoveKnobMounting: String? = null,
    @SerializedName("stoveMakeModel") val stoveMakeModel: String? = null,
    @SerializedName("stoveOrientation") val stoveOrientation: Int? = null,
    @SerializedName("stoveSetupComplete") val stoveSetupComplete: Boolean? = null,
    @SerializedName("uiAppType") val uiAppType: String? = null,
    @SerializedName("uiAppVersion") val uiAppVersion: String? = null
) : Parcelable

