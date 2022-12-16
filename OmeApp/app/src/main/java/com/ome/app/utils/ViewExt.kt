package com.ome.app.utils

import android.view.View

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
