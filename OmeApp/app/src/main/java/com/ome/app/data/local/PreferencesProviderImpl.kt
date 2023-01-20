package com.ome.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.ome.app.model.User


class PreferencesProviderImpl(context: Context) : PreferencesProvider {
    companion object {
        private const val PREFERENCES = "preferences"
        private const val USER_DATA_KEY = "user_data"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val USER_ID_KEY = "user_id"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override fun saveUserData(user: User?) = if (user == null) {
        sharedPreferences.edit().remove(USER_DATA_KEY).apply()
    } else {
        sharedPreferences.edit().putString(USER_DATA_KEY, Gson().toJson(user)).apply()
    }

    override fun getUserData(): User? {
        val json = sharedPreferences.getString(USER_DATA_KEY, "")
        return if (json == null) null else Gson().fromJson(json, User::class.java)
    }

    override fun saveAccessToken(accessToken: String?) = if (accessToken == null) {
        sharedPreferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    } else {
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply()
    }

    override fun getAccessToken(): String? = sharedPreferences.getString(ACCESS_TOKEN_KEY, "")

    override fun saveUserId(userId: String?) = if (userId == null) {
        sharedPreferences.edit().remove(USER_ID_KEY).apply()
    } else {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    override fun getUserId(): String? = sharedPreferences.getString(USER_ID_KEY, "")

}
