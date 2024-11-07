package com.ome.app.data.local

import android.content.Context

class ResourceProvider(val context: Context) {
    fun getString(resId: Int, vararg params: String): String = if (params.isNotEmpty()) {
        context.getString(resId, params)
    } else {
        context.getString(resId)
    }
}
