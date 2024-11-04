package com.ome.app.presentation.stove

import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupCompletedViewModel @Inject constructor() : BaseViewModel() {
    var brand = ""
    var type = ""
    var stoveOrientation = ""
}
