package com.ome.app.ui.stove

import com.ome.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupCompletedViewModel @Inject constructor() : BaseViewModel() {
    var brand = ""
    var type = ""
    var stoveOrientation = ""
}
