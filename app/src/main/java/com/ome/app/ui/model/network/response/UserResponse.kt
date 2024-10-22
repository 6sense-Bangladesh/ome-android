package com.ome.app.ui.model.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    val userId: String = "",
    val stoveId: String = "",
    val email: String = "",
    val numKnobs: Int = 0,
    val deviceTokens: List<String> = listOf(),
    val firstName: String? = null,
    val knobMacAddrs: List<String> = listOf(),
    val lastName: String? = null,
    val middleName: String? = null,
    val phone: String? = null,
    val stoveAutoOffMins: Int? = null,
    val stoveGasOrElectric: String? = null,
    val stoveMakeModel: String? = null,
    val stoveOrientation: Int? = null,
    val stoveSetupComplete: Boolean? = null,
    val uiAppType: String? = null,
    val uiAppVersion: String? = null
) : Parcelable
