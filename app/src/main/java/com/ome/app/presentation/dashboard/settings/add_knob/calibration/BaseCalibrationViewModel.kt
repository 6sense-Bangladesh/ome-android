package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import androidx.lifecycle.viewModelScope
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.SetCalibrationRequest
import com.ome.app.domain.model.network.request.Zone
import com.ome.app.domain.model.state.Rotation
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.KnobAngleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

abstract class BaseCalibrationViewModel(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository
) : BaseViewModel() {

    val knobAngleFlow = MutableStateFlow<Float?>(null)
    val zoneLiveData = SingleLiveEvent<Int>()

    var labelLiveData = SingleLiveEvent<Pair<CalibrationState, Float>>()

    var macAddress = ""

    var isDualKnob = false

    var offAngle: Float? = null

    var lowSingleAngle: Float? = null
    var mediumAngle: Float? = null
    var highSingleAngle: Float? = null

    var lowDualAngle: Float? = null
    var highDualAngle: Float? = null


    var currSetPosition: Int = 0

    var firstDiv: Int = 0
    var secondDiv: Int = 0

    var rotationDir: Int? = null

    val angleOffset = 15
    private val angleDualOffset = 31

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
            currSetPosition = currSetPosition,
            highSingleAngle = highSingleAngle,
            angleDualOffset = angleDualOffset
        )
        knobAngleFlow.value = result

    }

    fun initSubscriptions() {
        launch(ioContext) {
            webSocketManager.knobAngleFlow.filter { it?.macAddr == macAddress }.collect {
                it?.let {
                    val angle = it.value.toFloat()
                    if (!isDualKnob) {
                        knobAngleFlow.value = angle
                    } else {
                        if (offAngle != null) handleDualKnobUpdated(angle)
                        else knobAngleFlow.value = angle
                    }

                }
            }
        }
        launch(ioContext) {
            stoveRepository.knobsFlow.mapNotNull { dto -> dto.find { it.macAddr == macAddress } }.stateIn(viewModelScope).collect { foundKnob ->
                if (webSocketManager.knobAngleFlow.value == null) {
                    val angle = foundKnob.angle.toFloat()
                    if (!isDualKnob) {
                        knobAngleFlow.value = angle
                    } else {
                        if (offAngle != null) handleDualKnobUpdated(angle)
                        else knobAngleFlow.value = angle
                    }
                }
                zoneLiveData.postValue(foundKnob.stovePosition)
            }
        }
    }

    suspend fun setCalibration(){
        if (!isDualKnob) {
            if (offAngle != null && lowSingleAngle != null && mediumAngle != null && highSingleAngle != null && rotationDir != null) {
                stoveRepository.setCalibration(
                    SetCalibrationRequest(
                        offAngle = offAngle!!.toInt(),
                        rotationDir = rotationDir!!,
                        zones = arrayListOf(
                            Zone(
                                highAngle = highSingleAngle!!.toInt(),
                                mediumAngle = mediumAngle!!.toInt(),
                                lowAngle = lowSingleAngle!!.toInt(),
                                zoneName = "Single",
                                zoneNumber = 1
                            )
                        )
                    ),
                    macAddress
                )
            }
            else error("Something went wrong")
        } else {
            if (offAngle != null
                && lowSingleAngle != null
                && highSingleAngle != null
                && lowDualAngle != null
                && highDualAngle != null
//                && rotationDir != null
            ) {
                stoveRepository.setCalibration(
                    SetCalibrationRequest(
                        offAngle = offAngle!!.toInt(),
                        rotationDir = Rotation.DUAL.value,
                        zones = arrayListOf(
                            Zone(
                                highAngle = highSingleAngle!!.toInt(),
                                mediumAngle = KnobAngleManager.generateMediumAngle(
                                    highSingleAngle!!.toInt(),
                                    lowSingleAngle!!.toInt()
                                ),
                                lowAngle = lowSingleAngle!!.toInt(),
                                zoneName = "First",
                                zoneNumber = 1
                            ),
                            Zone(
                                highAngle = highDualAngle!!.toInt(),
                                mediumAngle = KnobAngleManager.generateMediumAngle(
                                    highDualAngle!!.toInt(),
                                    lowDualAngle!!.toInt()
                                ),
                                lowAngle = lowDualAngle!!.toInt(),
                                zoneName = "Second",
                                zoneNumber = 2
                            )
                        )
                    ),
                    macAddress
                )
            }
            else error("Something went wrong")
        }
    }

}
