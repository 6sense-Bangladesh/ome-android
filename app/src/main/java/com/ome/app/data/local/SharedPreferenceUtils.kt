package com.ome.app.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.ome.app.utils.tryGet

//val pref by lazy { OmeApplication.application.preference }

@Suppress("unused")
class SharedPreferenceUtils(val sharedPreferences: SharedPreferences) {

    var mEditor: SharedPreferences.Editor = sharedPreferences.edit()

    inline fun <reified T : Any> read(key: String, defaultValue: T): T {
        var value = sharedPreferences.getString(key, "default")
        value = if (value == "default") {
            defaultValue.toString()
        } else {
            value!!
        }
        return when (T::class) {
            String::class -> value as T
            Int::class -> value.toInt() as T
            Long::class -> value.toLong() as T
            Boolean::class -> value.toBoolean() as T
            else -> throw IllegalArgumentException("This default value type is not accepted only pass String, Long, Int, Boolean.")
        }
    }

    inline fun <reified T : Any> save(key: String, value: T) {
        mEditor.putString(key, value.toString()).also { mEditor.apply() }
    }

    inline fun <reified T> saveObject(key: String, obj: T) {
        save(key, Gson().toJson(obj))
    }

    inline fun <reified T> readObject(key: String): T? {
        val value = read(key, "")
        if (value.isEmpty()) return null
        return tryGet { Gson().fromJson(value, T::class.java) }
    }

    inline fun <reified T> clear(vararg restore: Pair<String, T>) {
        mEditor.clear()
        mEditor.apply()
        restore.forEach {
            when (T::class) {
                String::class -> save(it.first, it.second as String)
                Int::class -> save(it.first, it.second as Int)
                Long::class -> save(it.first, it.second as Long)
                Boolean::class -> save(it.first, it.second as Boolean)
                else -> throw IllegalArgumentException("This value type is not accepted only pass String, Long, Int, Boolean.")
            }
        }
    }

    fun clearAll() {
        mEditor.clear()
        mEditor.apply()
    }
}


object PrefKeys{
    const val AUTH_PARAMS = "auth_params"
}