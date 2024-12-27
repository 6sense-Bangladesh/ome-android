package com.ome.app.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.ome.app.R
import kotlinx.coroutines.*


fun View.makeGoneIf(predicate: Boolean) {
    if (predicate) makeGone() else makeVisible()
}

fun View.makeGoneIf(predicate: Boolean, or: Int = View.VISIBLE) {
    if (predicate) makeGone()
    else visibility = or
}

fun View.makeInvisibleIf(predicate: Boolean) {
    if (predicate) makeInvisible() else makeVisible()
}

fun View.makeVisibleIf(predicate: Boolean, or: Int = View.GONE) {
    if (predicate) makeVisible()
    else visibility = or
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun withDelay(delayMillis: Long, block: suspend CoroutineScope.() -> Unit) {
    MainScope().launch {
        delay(delayMillis)
        block()
    }
}

fun FragmentActivity.crateTopSnackBar(message: String): Snackbar {
    val snackBar = Snackbar.make(window.decorView, message, Snackbar.LENGTH_INDEFINITE)

    snackBar.view.background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 0F
    }

    snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = -30
            bottomMargin = -30
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        else
            gravity = Gravity.CENTER
    }

    // Adjust the position to top
    val layoutParams = snackBar.view.layoutParams as FrameLayout.LayoutParams
    layoutParams.gravity = Gravity.TOP
    layoutParams.setMargins(0.dp, 90.dp, 0.dp, 0.dp)
    snackBar.view.layoutParams = layoutParams
    return snackBar
}

