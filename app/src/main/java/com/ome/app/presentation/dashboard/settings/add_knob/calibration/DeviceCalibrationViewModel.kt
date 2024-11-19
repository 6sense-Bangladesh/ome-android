package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import android.util.Log
import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.InitCalibrationRequest
import com.ome.app.domain.model.state.Rotation
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.KnobAngleManager
import com.ome.app.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceCalibrationViewModel @Inject constructor(
    webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseCalibrationViewModel(webSocketManager, stoveRepository) {

    var currentCalibrationState = MutableStateFlow<CalibrationState?>(CalibrationState.OFF)

    val calibrationIsDoneLiveData = SingleLiveEvent<Boolean>()

    val previousScreenTriggered = SingleLiveEvent<Boolean>()

    fun setLabel() {
        val angle = knobAngleFlow.value
        angle?.let {
            if (isDualKnob) {
                if (KnobAngleManager.validateDualKnobAngle(
                        angle = angle,
                        offAngle = offAngle,
                        highSingleAngle = highSingleAngle,
                        lowSingleAngle = lowSingleAngle,
                        highDualAngle = highDualAngle,
                        lowDualAngle = lowDualAngle,
                        angleOffset = angleOffset
                    )
                ) {
                    currentCalibrationState.value?.let { step ->
                        when (step) {
                            CalibrationState.OFF -> {
                                offAngle = angle
                                launch(ioContext) {
                                    stoveRepository.initCalibration(
                                        InitCalibrationRequest(
                                            offAngle = angle.toInt(),
                                            rotationDir = Rotation.DUAL.value
                                        ), macAddress
                                    )
                                }
                                divideCircleBasedOnOffPosition()
                            }
                            CalibrationState.LOW_SINGLE ->  lowSingleAngle = angle
                            CalibrationState.LOW_DUAL -> lowDualAngle = angle
                            CalibrationState.HIGH_SINGLE -> highSingleAngle = angle
                            CalibrationState.HIGH_DUAL -> highDualAngle = angle
                            else -> Unit
                        }
                        currSetPosition++
                        labelLiveData.postValue(step to angle)
                        nextStep()
                    }

                } else {
                    defaultErrorLiveData.postValue(resourceProvider.getString(R.string.calibration_labels_error))
                }
            } else {
                if (KnobAngleManager.validateSingleKnobAngle(
                        angle = angle,
                        calibrationState = currentCalibrationState.value!!,
                        highSingleAngle = highSingleAngle,
                        mediumAngle = mediumAngle,
                        offAngle = offAngle,
                        lowSingleAngle = lowSingleAngle,
                        angleOffset = angleOffset
                    )
                ) {
                    currentCalibrationState.value?.let { step ->
                        when (step) {
                            CalibrationState.OFF -> {
                                offAngle = angle
                                launch(ioContext) {
                                    rotationDir?.let { dir ->
                                        stoveRepository.initCalibration(
                                            InitCalibrationRequest(
                                                offAngle = angle.toInt(),
                                                rotationDir = dir
                                            ), macAddress
                                        )
                                    } ?: error("Something went wrong")
                                }
                            }
                            CalibrationState.LOW_SINGLE -> {
                                lowSingleAngle = angle
                                Log.i(TAG, "setLabel: calibration is done")
                                calibrationIsDoneLiveData.postValue(true)
                            }
                            CalibrationState.MEDIUM -> mediumAngle = angle
                            CalibrationState.HIGH_SINGLE -> highSingleAngle = angle
                            CalibrationState.HIGH_DUAL -> highDualAngle = angle
                            CalibrationState.LOW_DUAL -> lowDualAngle = angle
                        }
                        labelLiveData.postValue(step to angle)
                        nextStep()
                    }
                } else {
                    defaultErrorLiveData.postValue(resourceProvider.getString(R.string.calibration_labels_error))
                }
            }
        }
//            ?: run {
//            launch {
//                stoveRepository.getAllKnobs()
//                setLabel()
//            }
//        }

    }


    private fun divideCircleBasedOnOffPosition() {
        offAngle?.let { angle ->
            firstDiv = angle.toInt()
            secondDiv = angle.toInt() - 180
            if (secondDiv < 0) secondDiv += 360
        }
    }

    fun clearData() {
        firstDiv = 0
        secondDiv = 0
        currSetPosition = 0
        offAngle = null
        lowSingleAngle = null
        lowDualAngle = null
        mediumAngle = null
        highSingleAngle = null
        highDualAngle = null
        calibrationIsDoneLiveData.value = false
    }


    private fun nextStep() {
        if (!isDualKnob) {
            val currentIndex =
                calibrationStatesSequenceSingleZone.indexOf(currentCalibrationState.value)
            if (currentIndex == calibrationStatesSequenceSingleZone.size - 1) {
                calibrationIsDoneLiveData.postValue(true)
            } else {
                currentCalibrationState.value =(calibrationStatesSequenceSingleZone[currentIndex + 1])
            }
        } else {
            val currentIndex =
                calibrationStatesSequenceDualZone.indexOf(currentCalibrationState.value)
            if (currentIndex == calibrationStatesSequenceDualZone.size - 1) {
                calibrationIsDoneLiveData.postValue(true)
            } else {
                currentCalibrationState.value =(calibrationStatesSequenceDualZone[currentIndex + 1])
            }
        }
    }

    fun previousStep() {
        val currentIndex = if (!isDualKnob) {
            calibrationStatesSequenceSingleZone.indexOf(currentCalibrationState.value)
        } else {
            calibrationStatesSequenceDualZone.indexOf(currentCalibrationState.value)
        }
        if (currentIndex == 0) {
            previousScreenTriggered.postValue(true)
        } else {
            val step = if (!isDualKnob)
                calibrationStatesSequenceSingleZone.getOrNull(currentIndex - 1)
            else
                calibrationStatesSequenceDualZone.getOrNull(currentIndex - 1)

            when (step) {
                CalibrationState.OFF -> offAngle = null
                CalibrationState.LOW_SINGLE -> lowSingleAngle = null
                CalibrationState.MEDIUM -> mediumAngle = null
                CalibrationState.HIGH_SINGLE -> highSingleAngle = null
                CalibrationState.HIGH_DUAL -> highDualAngle = null
                CalibrationState.LOW_DUAL -> lowDualAngle = null
                null -> Unit
            }
            currentCalibrationState.value = step
            currSetPosition--
        }
    }
}

enum class CalibrationState(val positionName: String) {
    OFF("Off"),
    HIGH_SINGLE("High"),
    MEDIUM("Medium"),
    LOW_SINGLE("Low"),
    HIGH_DUAL("High"),
    LOW_DUAL("Low")
}
