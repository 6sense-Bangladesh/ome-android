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
import com.ome.app.utils.KnobAngleManager.processDualKnob2ProtectOffOpposite
import kotlinx.coroutines.flow.*
import kotlin.math.min

abstract class BaseCalibrationViewModel(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository
) : BaseViewModel() {

    val knobAngleFlow = MutableStateFlow<Float?>(null)
    val calibrationIsDoneFlow = MutableSharedFlow<Unit>()

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

    var isZoneStartFromRight: Boolean = false
    var isRightZone: Boolean = false

    var rotationDir: Int = -1

    val angleOffset = 15
    private val angleDualOffset = 31

    val initAngle = MutableStateFlow<Int?>(null)

    val calibrationStatesSequenceSingleZone = listOf(
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

    val calibrationConfirmationStatesSingleZone get() = listOf(
        CalibrationState.HIGH_SINGLE to highSingleAngle,
        CalibrationState.MEDIUM to mediumAngle,
        CalibrationState.LOW_SINGLE to lowSingleAngle,
        CalibrationState.OFF to offAngle
    )
    val calibrationConfirmationStatesDualZone get() = listOf(
        CalibrationState.HIGH_DUAL to highDualAngle,
        CalibrationState.LOW_DUAL to lowDualAngle,
        CalibrationState.MOVE_OFF to offAngle,
        CalibrationState.LOW_SINGLE to lowSingleAngle,
        CalibrationState.HIGH_SINGLE to highSingleAngle,
        CalibrationState.OFF to offAngle
    )

    open fun handleDualKnobUpdated(angle: Float) {
//        if(initAngle.value == null || offAngle == null || lowSingleAngle == null || highSingleAngle == null)
//            initAngle.value = angle.toInt()

        knobAngleFlow.value = if (isDualKnob) {
            if(offAngle != null && highSingleAngle == null){
                processDualKnob2ProtectOffOpposite(
                    newAngle = angle,
                    offAngle = offAngle!!.toInt(),
                    angleDualOffset = angleDualOffset
                )
            }
            else if(offAngle != null){
                KnobAngleManager.processDualKnobResult(
                    initAngle = MutableStateFlow(null),
                    newAngle = angle,
                    firstDiv = firstDiv,
                    angleDualOffset = angleDualOffset,
                    offAngle = offAngle!!.toInt(),
                    isRightZone = isRightZone
//                    if(lowSingleAngle != null || highSingleAngle != null) false
//                    else if(lowDualAngle != null || highDualAngle != null) true
//                    else null
                )
            } else angle
        } else angle
    }

    fun initSubscriptions() {
        launch {
            webSocketManager.knobAngleFlow.filter { it?.macAddr == macAddress }.collect {
                it?.let {
                    handleDualKnobUpdated(it.value.toFloat())
                }
            }
        }
        launch {
            stoveRepository.knobsFlow.mapNotNull { dto -> dto.find { it.macAddr == macAddress } }.stateIn(viewModelScope).collect { foundKnob ->
                if (webSocketManager.knobAngleFlow.value == null) {
                    handleDualKnobUpdated(foundKnob.angle.toFloat())
                }
                zoneLiveData.postValue(foundKnob.stovePosition)
            }
        }
    }

    suspend fun setCalibration(){
        if (!isDualKnob) {
            if (offAngle != null && lowSingleAngle != null && mediumAngle != null && highSingleAngle != null) {
                stoveRepository.setCalibration(
                    SetCalibrationRequest(
                        offAngle = offAngle!!.toInt(),
                        rotationDir = min(rotationDir, Rotation.CLOCKWISE.value),
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
        calibrationIsDoneFlow.emit(Unit)
    }

}
