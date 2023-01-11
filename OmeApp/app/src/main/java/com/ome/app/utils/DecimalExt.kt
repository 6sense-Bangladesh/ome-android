package com.ome.app.utils

import android.content.res.Resources
import android.util.TypedValue


val Int.dp: Int get() = (Resources.getSystem().displayMetrics.density * this).toInt()

val Float.dp: Float get() = (Resources.getSystem().displayMetrics.density * this)

val Float.pxToDp: Float get() = this / Resources.getSystem().displayMetrics.density

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

