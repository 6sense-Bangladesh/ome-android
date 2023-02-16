package com.ome.app.data.local

import android.content.Context

class ResourceProvider(val context: Context) {
    fun getString(resId: Int, params: String? = null): String = if (params != null) {
        context.getString(resId, params)
    } else {
        context.getString(resId)
    }
}
