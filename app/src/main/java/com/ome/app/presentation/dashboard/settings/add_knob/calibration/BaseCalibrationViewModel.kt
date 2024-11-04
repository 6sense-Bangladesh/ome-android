package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.KnobAngleManager

abstract class BaseCalibrationViewModel constructor(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val knobAngleLiveData = SingleLiveEvent<Float>()
    val zoneLiveData = SingleLiveEvent<Int>()

    var labelLiveData = SingleLiveEvent<Pair<CalibrationState, Float>>()

    var macAddress = ""

    var isDualKnob = false

    var offAngle: Float? = null
    var lowSingleAngle: Float? = null
    var mediumAngle: Float? = null
    var highSingleAngle: Float? = null

    var lowDualAngle: Float? = null
    var mediumDualAngle: Float? = null
    var highDualAngle: Float? = null


    var currSetting: Int = 0

    var firstDiv: Int = 0
    var secondDiv: Int = 0

    var rotationDir: Int? = null

    val angleOffset = 15
    val angleDualOffset = 31

    val calibrationStatesSequenceSingleZone = arrayListOf(
        CalibrationState.OFF,
        CalibrationState.HIGH_SINGLE,
        CalibrationState.MEDIUM,
        CalibrationState.LOW_SINGLE
    )

    val calibrationStatesSequenceDualZone = arrayListOf(
        CalibrationState.OFF,
        CalibrationState.HIGH_SINGLE,
        CalibrationState.LOW_SINGLE,
        CalibrationState.HIGH_DUAL,
        CalibrationState.LOW_DUAL
    )


    open fun handleDualKnobUpdated(value: Float) {

        val result = KnobAngleManager.processDualKnobResult(
            angleValue = value,
            firstDiv = firstDiv,
            secondDiv = secondDiv,
            currentStepAngle = labelLiveData.value?.second?.toInt(),
            currSetting = currSetting,
            highSingleAngle = highSingleAngle,
            angleDualOffset = angleDualOffset
        )
        knobAngleLiveData.postValue(result)

    }

    fun initSubscriptions() {
        launch(ioContext) {
            webSocketManager.knobAngleFlow.collect {
                it?.let {
                    val angle = it.value.toFloat()
                    if (!isDualKnob) {
                        knobAngleLiveData.postValue(angle)
                    } else {
                        if (offAngle != null) {
                            handleDualKnobUpdated(angle)
                        } else {
                            knobAngleLiveData.postValue(angle)
                        }
                    }

                }
            }
        }
        launch(ioContext) {
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
