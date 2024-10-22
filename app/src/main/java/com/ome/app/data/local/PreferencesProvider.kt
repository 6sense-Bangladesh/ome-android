package com.ome.app.data.local

import com.ome.app.ui.model.network.response.UserResponse


interface PreferencesProvider {
    fun saveUserData(user: UserResponse?)

    fun getUserData(): UserResponse

    fun saveAccessToken(accessToken: String?)

    fun clearData()

    fun getAccessToken(): String?

    fun saveUserId(userId: String?)

    fun getUserId(): String?
}
