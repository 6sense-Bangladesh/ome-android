package com.ome.app.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
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

fun FragmentActivity.showTopSnackBar(message: String?) {
    if (message == null) return
    val snackBar = Snackbar.make(window.decorView, message, Snackbar.LENGTH_LONG)

    // Set the background color to red
    snackBar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

    // Adjust the position to top
    val layoutParams = snackBar.view.layoutParams as FrameLayout.LayoutParams
    layoutParams.gravity = Gravity.TOP
    snackBar.view.layoutParams = layoutParams

    snackBar.show()
}

