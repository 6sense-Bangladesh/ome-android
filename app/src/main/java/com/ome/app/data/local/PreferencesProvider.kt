package com.ome.app.data.local

import com.ome.app.domain.model.network.response.UserResponse


interface PreferencesProvider {
    fun saveUserData(user: UserResponse?)

    fun getUserData(): UserResponse

    fun saveAccessToken(accessToken: String?)

    fun clearData()

    fun getAccessToken(): String?

    fun saveUserId(userId: String?)

    fun getUserId(): String?

    fun getTimer(macAddress: String): Long

    fun setTimer(macAddress: String, timeStamp: Long)
}

data class Timer(val macAddress: String, val timeStamp: Long)