package com.ome.app.ui.model.network.request

data class CreateUserRequest(
    val deviceTokens: List<String>? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val stoveOrientation: Int = -1,
    val uiAppType: String? = null,
    val uiAppVersion: String? = null,
    val userId: String? = null
)