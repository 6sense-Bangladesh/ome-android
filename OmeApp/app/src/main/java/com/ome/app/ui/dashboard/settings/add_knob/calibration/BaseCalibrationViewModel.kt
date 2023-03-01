package com.ome.app.ui.dashboard.settings.add_knob.calibration

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager

abstract class BaseCalibrationViewModel constructor(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val knobAngleLiveData = SingleLiveEvent<Float>()
    val zoneLiveData = SingleLiveEvent<Int>()

    var macAddress = ""

    var isDualKnob = false

    var offAngle: Float? = null
    var lowAngle: Float? = null
    var mediumAngle: Float? = null
    var highAngle: Float? = null

    var rotationDir: Int? = null

    val angleOffset = 15

    val calibrationStatesSequence = arrayListOf(
        CalibrationState.OFF,
        CalibrationState.HIGH,
        CalibrationState.MEDIUM,
        CalibrationState.LOW
    )

    fun initSubscriptions() {
        launch(dispatcher = ioContext) {
            webSocketManager.knobAngleFlow.collect {
                it?.let {
                    knobAngleLiveData.postValue(it.value.toFloat())
                }
            }
        }
        launch(dispatcher = ioContext) {
            stoveRepository.knobsFlow.collect { knobs ->
                knobs?.let {
                    val foundKnob = knobs.firstOrNull { it.macAddr == macAddress }
                    foundKnob?.let {
                        if (webSocketManager.knobAngleFlow.value == null) {
                            knobAngleLiveData.postValue(it.angle.toFloat())
                        }
                        zoneLiveData.postValue(foundKnob.stovePosition)
                    }
                }
            }
        }
    }
}
