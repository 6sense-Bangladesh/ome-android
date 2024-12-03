package com.ome.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.model.network.websocket.MacAddress


class PreferencesProviderImpl(context: Context) : PreferencesProvider {
    companion object {
        private const val PREFERENCES = "preferences"
        private const val USER_DATA_KEY = "user_data"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val USER_ID_KEY = "user_id"
        private const val TIMER_KEY = "timer_id"
        private const val TIMER_PAUSE_KEY = "timer_pause_id"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override val utils: SharedPreferenceUtils
        get() = SharedPreferenceUtils(sharedPreferences)

    override fun saveUserData(user: UserResponse?) = if (user == null) {
        sharedPreferences.edit().remove(USER_DATA_KEY).apply()
    } else {
        sharedPreferences.edit().putString(USER_DATA_KEY, Gson().toJson(user)).apply()
    }

    override fun getUserData(): UserResponse {
        val json = sharedPreferences.getString(USER_DATA_KEY, "{}") ?: "{}"
        return Gson().fromJson(json, UserResponse::class.java)
    }

    override fun saveAccessToken(accessToken: String?) = if (accessToken == null) {
        sharedPreferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    } else {
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply()
    }

    override fun clearData() = sharedPreferences.edit().clear().apply()

    override fun getAccessToken(): String? = sharedPreferences.getString(ACCESS_TOKEN_KEY, "")

    override fun saveUserId(userId: String?) = if (userId == null) {
        sharedPreferences.edit().remove(USER_ID_KEY).apply()
    } else {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    override fun getUserId(): String? = sharedPreferences.getString(USER_ID_KEY, "")

    override fun setTimer(macAddress: String, timeStamp: Long) {
        getTimerMap().toMutableMap().apply {
            put(macAddress, timeStamp)
            sharedPreferences.edit().putString(TIMER_KEY, Gson().toJson(toMap())).apply()
        }
    }

    override fun getTimer(macAddress: String): Long {
        val map = getTimerMap()
        return map.getOrDefault(macAddress, 0L)
    }

    private fun getTimerMap(): Map<MacAddress, Long> {
        val empty = Gson().toJson(emptyMap<MacAddress, Long>())
        val json = sharedPreferences.getString(TIMER_KEY, empty) ?: empty
        val typeMap = object : TypeToken<Map<MacAddress, Long>>() {}.type
        return try {
            Gson().fromJson<Map<MacAddress, Long>>(json, typeMap).orEmpty()
        } catch (e: JsonSyntaxException) {
            emptyMap()
        }
    }

    private fun getPauseTimerMap(): Map<MacAddress, Triple<Int, Int, Int>> {
        val empty = Gson().toJson(emptyMap<MacAddress, Triple<Int, Int, Int>>())
        val json = sharedPreferences.getString(TIMER_PAUSE_KEY, empty) ?: empty
        val typeMap = object : TypeToken<Map<MacAddress, Triple<Int, Int, Int>>>() {}.type
        return try {
            Gson().fromJson<Map<MacAddress, Triple<Int, Int, Int>>>(json, typeMap).orEmpty()
        } catch (e: JsonSyntaxException) {
            emptyMap()
        }
    }

    override fun setPauseTime(macAddress: String, time: Triple<Int, Int, Int>?) {
        getPauseTimerMap().toMutableMap().apply {
            time?.also { put(macAddress, it) } ?: remove(macAddress)
            sharedPreferences.edit().putString(TIMER_PAUSE_KEY, Gson().toJson(toMap())).apply()
        }
    }

    override fun getPauseTime(macAddress: String): Triple<Int, Int, Int> {
        val map = getPauseTimerMap()
        return map.getOrDefault(macAddress, Triple(0,0,0))
    }
}
