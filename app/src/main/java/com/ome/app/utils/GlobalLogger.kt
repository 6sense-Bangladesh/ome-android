package com.ome.app.utils

import android.util.Log
import com.ome.app.BuildConfig

var LOGGING_ENABLED = BuildConfig.IS_INTERNAL_TESTING

fun Any.loge(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.e("e".plus(TAG), msg ?: "null")
    }
}

fun Any.loge(msg: String?, throwable: Throwable?) {
    if (LOGGING_ENABLED) {
        Log.e("e".plus(TAG), msg ?: "null", throwable ?: Throwable())
    }
}

fun Any.logd(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.d("d".plus(TAG), msg ?: "null")
    }
}

fun Any.logw(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.w("w".plus(TAG), msg ?: "null")
    }
}

fun Any.logi(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.i("i".plus(TAG), msg ?: "null")
    }
}

fun Any.logv(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.v("v".plus(TAG), msg ?: "null")
    }
}

fun Any.logwtf(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.wtf("f".plus(TAG), msg ?: "null")
    }
}

val <T : Any> T.TAG: String
    get() = if (this::class.java.simpleName.length > 16)
        "OME_".plus(this::class.java.simpleName.substring(0, 15))
    else
        "OME_".plus(this::class.java.simpleName)
