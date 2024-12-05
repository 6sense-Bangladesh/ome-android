package com.ome.app.presentation.launch

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthParams(
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("current_password") val currentPassword: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("code") var code: String = "",
    @SerializedName("is_forgot_password") val isForgotPassword: Boolean = false
) : Parcelable