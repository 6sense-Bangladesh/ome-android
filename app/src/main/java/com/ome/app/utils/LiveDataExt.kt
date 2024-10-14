package com.ome.app.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


fun <T> Fragment.subscribe(liveData: (LiveData<T>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(viewLifecycleOwner, Observer {
        if (it != null) {
            onNext(it)
        }
    })
}

fun <T> LiveData<T>.mutable(): MutableLiveData<T> =
    (this as? MutableLiveData) ?: throw IllegalArgumentException("LiveData is not mutable")

fun <T> AppCompatActivity.subscribe(liveData: (LiveData<T>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(this) {
        if (it != null) {
            onNext(it)
        }
    }
}


fun <T> Fragment.subscribe(flow: (Flow<T?>)?, onNext: (t: T) -> Unit) {
    flow ?: return
    viewLifecycleOwner.lifecycle.coroutineScope.launch {
        flow.collect {
            if (it != null) {
                onNext(it)
            }
        }
    }
}


fun <T> AppCompatActivity.subscribe(flow: (Flow<T?>)?, onNext: (t: T) -> Unit) {
    flow ?: return
    lifecycle.coroutineScope.launch {
        flow.collect {
            if (it != null) {
                onNext(it)
            }
        }
    }
}
