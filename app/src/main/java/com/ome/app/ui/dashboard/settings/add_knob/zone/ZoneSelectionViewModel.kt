package com.ome.app.ui.dashboard.settings.add_knob.zone

import com.ome.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ZoneSelectionViewModel @Inject constructor(): BaseViewModel() {
    var zoneNumber = 0
    var isDualKnob = false
}
