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

    fun setTimer(macAddress: String, timeStamp: Long)
    fun getTimer(macAddress: String): Long

    fun setPauseTime(macAddress: String, time: Triple<Int, Int, Int>? = null)
    fun getPauseTime(macAddress: String): Triple<Int, Int, Int>
}

data class Timer(val macAddress: String, val timeStamp: Long)