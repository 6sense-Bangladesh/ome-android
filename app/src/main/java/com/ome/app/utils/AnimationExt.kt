@file:Suppress("unused")

package com.ome.app.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
fun View.setBounceClickListener(onClick: ((View) -> Unit)? = null) {
    val mainScope = MainScope()
    var delay = 0L
    setOnClickListener {
        mainScope.launch {
            delay(200L)
            onClick?.invoke(it)
        }
    }
    setOnTouchListener { v, event ->
        mainScope.launch {
            if (event.action == MotionEvent.ACTION_DOWN) {
                delay(delay)
                val scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.95f)
                val scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.95f)
                scaleDownX.duration = 150L
                scaleDownY.duration = 150L

                val scaleDown = AnimatorSet()
                scaleDown.play(scaleDownX).with(scaleDownY)
                scaleDown.start()
                delay = 150L
            } else {
                val delayNeeded =
                    if (event.action == MotionEvent.ACTION_UP || !event.isInside(v)) delay else 350L
                delay(delayNeeded)
                delay = 0L
                val scaleDownX2 = ObjectAnimator.ofFloat(v, "scaleX", 1f)
                val scaleDownY2 = ObjectAnimator.ofFloat(v, "scaleY", 1f)
                scaleDownX2.duration = 130L
                scaleDownY2.duration = 130L

                val scaleDown2 = AnimatorSet()
                scaleDown2.play(scaleDownX2).with(scaleDownY2)
                scaleDown2.start()
            }
        }
        false
    }
}

fun MotionEvent.isInside(view: View): Boolean {
    if (view.width == 0 || view.height == 0) return false
    return try {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val viewMaxX = viewLocation[0] + view.width - 1
        val viewMaxY = viewLocation[1] + view.height - 1
        (rawX <= viewMaxX && rawX >= viewLocation[0] && rawY <= viewMaxY && rawY >= viewLocation[1])
    } catch (e: Exception) {
        false
    }
}

fun View.showOrHideWithAnimation() {
    if (this.isVisible) hideWithFadeOutAnimation()
    else showWithFadeInAnimation()
}

fun View.showWithFadeInAnimation() {
    if (visibility == View.VISIBLE) return // No need to animate if already visible
    val animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    animator.duration = 500
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            visibility = View.VISIBLE
        }
    })
    animator.start()
}

fun View.hideWithFadeOutAnimation() {
    if (visibility == View.GONE) return // No need to animate if already gone
    val animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    animator.duration = 200
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            visibility = View.GONE
        }
    })
    animator.start()
}

fun View.animateUp(heightDifference: Int = this.height) {
    val animator = ObjectAnimator.ofFloat(this, "translationY", heightDifference.toFloat(), 0f)
    animator.duration = 500
    animator.start()
}

fun View.animateVisibility(doOnEnd: (isVisible: Boolean) -> Unit = {}, doOnStart: (isVisible: Boolean) -> Unit = {}) {
    if (height == 0 || !isVisible) {
        doOnStart(true)
        visible()
        Log.d("animateVisibility:", "if View.VISIBLE")
        visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(0, minimumHeight)
        valueAnimator.duration = 500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val params = this.layoutParams
            params.height = animatedValue
            this.layoutParams = params
        }
        valueAnimator.doOnEnd { doOnEnd.invoke(true) }
//        valueAnimator.addListener(onEnd =onFullyVisible)
        valueAnimator.start()
    } else {
        doOnStart(false)
        Log.d("animateVisibility:", " else")
        val valueAnimator = ValueAnimator.ofInt(this.measuredHeight, 0)
        valueAnimator.duration = 500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val params = this.layoutParams
            params.height = animatedValue
            this.layoutParams = params
        }
        valueAnimator.doOnEnd {
            doOnEnd.invoke(false)
            gone()
        }
        valueAnimator.start()
    }
}

fun View.animateInvisible() {
    if (height != 0 || isVisible) {
        Log.d("a", "animateInvisible")
        val valueAnimator = ValueAnimator.ofInt(this.measuredHeight, 0)
        valueAnimator.duration = 600L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val params = this.layoutParams
            params.height = animatedValue
            this.layoutParams = params
        }
        valueAnimator.start()
    }
}

fun View.animateVisible() {
    if (height == 0 || !isVisible) {
        Log.d("animateVisibility:", "if View.VISIBLE")
        visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(0, minimumHeight)
        valueAnimator.duration = 500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val params = this.layoutParams
            params.height = animatedValue
            this.layoutParams = params
        }
        valueAnimator.start()
    }
}

var ProgressBar.animateProgress: Int
    get()= this.progress
    set(value) {
        ObjectAnimator.ofInt(this, "progress", value)
            .setDuration(700L)
            .start()
    }
var TextView.animateText: Int?
    get()= text.toString().toIntOrNull()
    set(value) {
        if (value == null) return
        val owner = findViewTreeLifecycleOwner() ?: return
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.RESUMED){
                for(i in 0 .. value step  500) {
                    delay(45)
                    text = value.toStringLocale()
                }
                text = value.toStringLocale()
            }
        }
    }
