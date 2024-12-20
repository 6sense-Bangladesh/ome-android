package com.ome.app.presentation.dashboard.my_stove

import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MyStoveViewModel @Inject constructor(): BaseViewModel(){
    var lastStovePositions = listOf<Int>()
}
