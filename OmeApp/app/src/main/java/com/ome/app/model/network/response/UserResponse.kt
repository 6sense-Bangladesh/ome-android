package com.ome.app.model.network.response

data class UserResponse(
    val deviceTokens: List<String> = listOf(),
    val email: String?,
    val firstName: String?,
    val knobMacAddrs: List<String> = listOf(),
    val lastName: String?,
    val middleName: String?,
    val numKnobs: Int?,
    val phone: String?,
    val stoveAutoOffMins: Int?,
    val stoveGasOrElectric: String?,
    val stoveId: String?,
    val stoveMakeModel: String?,
    val stoveOrientation: Int?,
    val stoveSetupComplete: String?,
    val uiAppType: String?,
    val uiAppVersion: String?,
    val userId: String?
)
