package com.ome.app.utils

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration


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

fun withDelay(duration: Duration, block: suspend CoroutineScope.() -> Unit) {
    MainScope().launch {
        delay(duration)
        block()
    }
}

