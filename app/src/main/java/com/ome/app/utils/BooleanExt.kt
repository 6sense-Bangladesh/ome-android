package com.ome.app.utils

import android.view.View

fun Boolean.toVisibility(useGone: Boolean = true): Int {
    return when (this) {
        true -> View.VISIBLE
        false -> {
            if (useGone) {
                View.GONE
            } else {
                View.INVISIBLE
            }
        }
    }
}
