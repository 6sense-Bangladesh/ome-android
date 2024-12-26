package com.ome.app.presentation.dashboard.settings.add_knob.zone

import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ZoneSelectionViewModel @Inject constructor(): BaseViewModel() {
    var isDualKnob = false
}
