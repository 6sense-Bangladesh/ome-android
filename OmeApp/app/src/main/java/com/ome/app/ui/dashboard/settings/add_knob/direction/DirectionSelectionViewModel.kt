package com.ome.app.ui.dashboard.settings.add_knob.direction

import com.ome.app.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DirectionSelectionViewModel @Inject constructor(): BaseViewModel() {
    var clockwiseDir = -1
}
