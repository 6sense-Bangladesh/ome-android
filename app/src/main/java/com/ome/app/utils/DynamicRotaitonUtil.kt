package com.ome.app.utils

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.ComponentActivity

/**
 * Checks the screen orientation & sets the orientation to portrait or landscape depending on the screen size
 * @return true if the screen is a tablet, false otherwise
 */
fun ComponentActivity.dynamicRotation() {
    val screenLayout = resources.configuration.screenLayout
    val screenSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    val isTablet = screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE

    requestedOrientation = if (isTablet) {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    } else {
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}


fun isInTablet(context: Context): Boolean {
    val screenLayout = context.resources.configuration.screenLayout
    val screenSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE
}