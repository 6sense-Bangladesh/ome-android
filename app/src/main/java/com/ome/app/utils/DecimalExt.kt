@file:Suppress("unused")

package com.ome.app.utils

import android.content.res.Resources
import android.util.TypedValue

/**Converts DP into pixel */
val Int.dp: Int get() = (Resources.getSystem().displayMetrics.density * this).toInt()
/**Converts DP into pixel */
val Float.dp: Float get() = (Resources.getSystem().displayMetrics.density * this)

/** Converts pixel into dp */
val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
/** Converts pixel into dp */
val Float.px: Float get() = this / Resources.getSystem().displayMetrics.density

inline val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

inline val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

