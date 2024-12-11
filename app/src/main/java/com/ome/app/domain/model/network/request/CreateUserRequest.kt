package com.ome.app.domain.model.network.request

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("deviceTokens")
    val deviceTokens: List<String>? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("stoveOrientation")
    val stoveOrientation: Int? = null,
    @SerializedName("uiAppType")
    val uiAppType: String? = null,
    @SerializedName("uiAppVersion")
    val uiAppVersion: String? = null,
    @SerializedName("userId")
    val userId: String? = null
)
