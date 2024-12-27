package com.ome.app.presentation.dashboard.settings.add_knob.installation

import com.ome.app.data.ConnectionListener
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MetalPlateInstallationViewModel @Inject constructor(
    connectionListener: ConnectionListener
) : BaseViewModel(){
    init {
        launch {
            delay(15.seconds)
            connectionListener.shouldReactOnChanges = true
        }
    }
}