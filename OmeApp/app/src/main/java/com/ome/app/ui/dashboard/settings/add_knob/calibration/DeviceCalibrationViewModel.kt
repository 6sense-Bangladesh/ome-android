package com.ome.app.ui.dashboard.settings.add_knob.calibration

import android.util.Log
import com.ome.Ome.R
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.model.network.request.InitCalibrationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceCalibrationViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseCalibrationViewModel(webSocketManager, stoveRepository, resourceProvider) {


    var currentCalibrationStateLiveData = SingleLiveEvent<CalibrationState>()

    val calibrationIsDoneLiveData = SingleLiveEvent<Boolean>()

    val previousScreenTriggered = SingleLiveEvent<Boolean>()

    fun setLabel() {
        val angle = knobAngleLiveData.value
        angle?.let {
            if (isDualKnob) {
                if (validateDualKnobAngle(angle, currentCalibrationStateLiveData.value!!)) {
                    currentCalibrationStateLiveData.value?.let { step ->
                        when (step) {
                            CalibrationState.OFF -> {
                                offAngle = angle
                                launch(dispatcher = ioContext) {
                                    stoveRepository.initCalibration(
                                        InitCalibrationRequest(
                                            offAngle = angle.toInt(),
                                            rotationDir = 2
                                        ), macAddress
                                    )
                                }
                                divideCircleBasedOnOffPosition()
                            }
                            CalibrationState.LOW_SINGLE -> {
                                lowSingleAngle = angle
                            }
                            CalibrationState.LOW_DUAL -> {
                                lowDualAngle = angle
                            }
                            CalibrationState.HIGH_SINGLE -> {
                                highSingleAngle = angle
                            }
                            CalibrationState.HIGH_DUAL -> {
                                highDualAngle = angle
                            }
                            else -> {}
                        }
                        currSetting++
                        labelLiveData.postValue(step to angle)
                        nextStep()
                    }

                } else {
                    defaultErrorLiveData.postValue(resourceProvider.getString(R.string.calibration_labels_error))
                }
            } else {
                if (validateSingleKnobAngle(angle, currentCalibrationStateLiveData.value!!)) {
                    currentCalibrationStateLiveData.value?.let { step ->
                        when (step) {
                            CalibrationState.OFF -> {
                                offAngle = angle
                                launch(dispatcher = ioContext) {
                                    rotationDir?.let { dir ->
                                        stoveRepository.initCalibration(
                                            InitCalibrationRequest(
                                                offAngle = angle.toInt(),
                                                rotationDir = dir
                                            ), macAddress
                                        )
                                    }
                                }
                            }
                            CalibrationState.LOW_SINGLE -> {
                                lowSingleAngle = angle
                                Log.i(
                                    DeviceCalibrationViewModel::class.simpleName,
                                    "setLabel: calibarion is done"
                                )
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

    }


    private fun divideCircleBasedOnOffPosition() {
        offAngle?.let { angle ->
            firstDiv = angle.toInt()
            secondDiv = angle.toInt() - 180

            if (secondDiv < 0) {
                secondDiv += 360
            }
            rightAllowedZoneStartAngle = angle + angleOffset
            rightAllowedZoneEndAngle = angle + 180 - angleOffset

            if (rightAllowedZoneStartAngle > 360) {
                rightAllowedZoneStartAngle -= 360
            }

            if (rightAllowedZoneEndAngle > 360) {
                rightAllowedZoneEndAngle -= 360
            }

            leftAllowedZoneStartAngle = angle - angleOffset
            leftAllowedZoneEndAngle = angle - 180 + angleOffset

            if (leftAllowedZoneStartAngle < 0) {
                leftAllowedZoneStartAngle += 360
            }

            if (leftAllowedZoneEndAngle < 0) {
                leftAllowedZoneEndAngle += 360
            }
        }

    }

    fun clearData() {
        firstDiv = 0
        secondDiv = 0
        currSetting = 0
        leftAllowedZoneStartAngle = 0f
        leftAllowedZoneEndAngle = 0f
        rightAllowedZoneStartAngle = 0f
        rightAllowedZoneEndAngle = 0f
        offAngle = null
        lowSingleAngle = null
        lowDualAngle = null
        mediumAngle = null
        highSingleAngle = null
        highDualAngle = null
        calibrationIsDoneLiveData.value = false
    }
    private fun validateSingleKnobAngle(
        angle: Float,
        calibrationState: CalibrationState
    ): Boolean {
        when (calibrationState) {
            CalibrationState.OFF -> {}
            CalibrationState.HIGH_SINGLE -> {}
            CalibrationState.MEDIUM -> {}
            CalibrationState.LOW_SINGLE -> {
                if (highSingleAngle != null && mediumAngle != null) {
                    if (highSingleAngle!! > mediumAngle!!) {
                        if (angle in mediumAngle!!..highSingleAngle!!) {
                            return false
                        }
                    } else {
                        if (angle in highSingleAngle!!..mediumAngle!!) {
                            return false
                        }
                    }
                }
            }
            else -> {}
        }
        offAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        lowSingleAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        mediumAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        highSingleAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        return true
    }

    private fun validateDualKnobAngle(
        angle: Float,
        calibrationState: CalibrationState
    ): Boolean {
        when (calibrationState) {
            CalibrationState.OFF -> {}
            CalibrationState.HIGH_SINGLE -> {}
            CalibrationState.LOW_SINGLE -> {}
            CalibrationState.MEDIUM -> {}

            else -> {}
        }
        offAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        lowSingleAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        mediumAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        highSingleAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        return true
    }

    private fun nextStep() {
        if (!isDualKnob) {
            val currentIndex =
                calibrationStatesSequenceSingleZone.indexOf(currentCalibrationStateLiveData.value)
            if (currentIndex == calibrationStatesSequenceSingleZone.size - 1) {
                calibrationIsDoneLiveData.postValue(true)
            } else {
                currentCalibrationStateLiveData.postValue(calibrationStatesSequenceSingleZone[currentIndex + 1])
            }
        } else {
            val currentIndex =
                calibrationStatesSequenceDualZone.indexOf(currentCalibrationStateLiveData.value)
            if (currentIndex == calibrationStatesSequenceDualZone.size - 1) {
                calibrationIsDoneLiveData.postValue(true)
            } else {
                currentCalibrationStateLiveData.postValue(calibrationStatesSequenceDualZone[currentIndex + 1])
            }
        }
    }

    fun previousStep() {
        val currentIndex = if (!isDualKnob) {
            calibrationStatesSequenceSingleZone.indexOf(currentCalibrationStateLiveData.value)
        } else {
            calibrationStatesSequenceDualZone.indexOf(currentCalibrationStateLiveData.value)
        }
        if (currentIndex == 0) {
            previousScreenTriggered.postValue(true)
        } else {
            val step = if (!isDualKnob) {
                calibrationStatesSequenceSingleZone[currentIndex - 1]
            } else {
                calibrationStatesSequenceDualZone[currentIndex - 1]
            }

            when (step) {
                CalibrationState.OFF -> offAngle = null
                CalibrationState.LOW_SINGLE -> lowSingleAngle = null
                CalibrationState.MEDIUM -> mediumAngle = null
                CalibrationState.HIGH_SINGLE -> highSingleAngle = null
                CalibrationState.HIGH_DUAL -> highDualAngle = null
                CalibrationState.LOW_DUAL -> lowDualAngle = null
            }
            currentCalibrationStateLiveData.postValue(step)
            currSetting--
        }
    }
}

enum class CalibrationState(val positionName: String) {
    OFF("OFF"), HIGH_SINGLE("HIGH"), MEDIUM("MEDIUM"), LOW_SINGLE("LOW"), HIGH_DUAL("HIGH"), LOW_DUAL("LOW")
}
