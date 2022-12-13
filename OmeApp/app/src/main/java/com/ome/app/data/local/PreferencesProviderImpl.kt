package com.ome.app.data.local

import android.content.Context

class PreferencesProviderImpl(context: Context) : PreferencesProvider {
    companion object {
        private const val PREFERENCES = "preferences"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

}
