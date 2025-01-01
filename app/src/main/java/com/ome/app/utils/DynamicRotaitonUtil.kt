package com.ome.app.utils

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import com.google.android.gms.common.util.DeviceProperties.isTablet

/**
 * Checks the screen orientation & sets the orientation to portrait or landscape depending on the screen size
 * @return true if the screen is a tablet, false otherwise
 */
fun ComponentActivity.dynamicRotation() {
    requestedOrientation = if (isTablet(resources)) {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    } else {
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
