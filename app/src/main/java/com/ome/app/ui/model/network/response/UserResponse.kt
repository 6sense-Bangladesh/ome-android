package com.ome.app.ui.model.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    val deviceTokens: List<String> = listOf(),
    val email: String? = null,
    val firstName: String? = null,
    val knobMacAddrs: List<String> = listOf(),
    val lastName: String? = null,
    val middleName: String? = null,
    val numKnobs: Int? = null,
    val phone: String? = null,
    val stoveAutoOffMins: Int? = null,
    val stoveGasOrElectric: String? = null,
    val stoveId: String? = null,
    val stoveMakeModel: String? = null,
    val stoveOrientation: Int? = null,
    val stoveSetupComplete: String? = null,
    val uiAppType: String? = null,
    val uiAppVersion: String? = null,
    val userId: String? = null
) : Parcelable
