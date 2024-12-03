package com.ome.app.utils

object Constants {
    const val INSTRUCTION_URL = "https://ome.helpar.app/m/ome-smart-stove-gen2/hq"
    const val SUPPORT_EMAIL = "support@omekitchen.com"

    const val VERIFICATION_KEY = "verification"
    val TWO_MINUTES_MILLIS: Long
        get() = System.currentTimeMillis() + 120 * 1000
}
