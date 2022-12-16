package com.ome.app.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


fun globalToast(context: Context, text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, text, duration).show()

fun FragmentActivity.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    globalToast(this, text, duration)

fun FragmentActivity.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    globalToast(this, getString(resId), duration)

fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(text, duration)

fun Fragment.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(resId, duration)

