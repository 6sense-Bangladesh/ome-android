package com.ome.app.model.network

data class UserResponse(
    val deviceTokens: List<Any>,
    val email: String,
    val firstName: String,
    val knobMacAddrs: List<Any>,
    val lastName: String,
    val middleName: Any,
    val numKnobs: Int,
    val phone: String,
    val stoveAutoOffMins: Int,
    val stoveGasOrElectric: String,
    val stoveId: String,
    val stoveMakeModel: String,
    val stoveOrientation: Int,
    val stoveSetupComplete: Any,
    val uiAppType: String,
    val uiAppVersion: String,
    val userId: String
)
