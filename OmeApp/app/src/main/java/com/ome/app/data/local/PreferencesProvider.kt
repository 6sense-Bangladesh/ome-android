package com.ome.app.data.local

import com.ome.app.model.local.User


interface PreferencesProvider {
    fun saveUserData(user: User?)

    fun getUserData(): User?

    fun saveAccessToken(accessToken: String?)

    fun clearData()

    fun getAccessToken(): String?

    fun saveUserId(userId: String?)

    fun getUserId(): String?
}
