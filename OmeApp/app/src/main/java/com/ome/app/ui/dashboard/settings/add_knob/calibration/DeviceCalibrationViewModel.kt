package com.ome.app.ui.dashboard.settings.add_knob.calibration

import android.util.Log
import com.ome.Ome.R
import com.ome.app.base.SingleLiveEvent
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

    var labelLiveData = SingleLiveEvent<Pair<CalibrationState, Float>>()

    val calibrationIsDoneLiveData = SingleLiveEvent<Boolean>()

    val previousScreenTriggered = SingleLiveEvent<Boolean>()

    fun setLabel() {
        val angle = knobAngleLiveData.value
        angle?.let {
            if (validateAngle(angle, currentCalibrationStateLiveData.value!!)) {
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
                        CalibrationState.LOW -> {
                            lowAngle = angle
                            Log.i(
                                DeviceCalibrationViewModel::class.simpleName,
                                "setLabel: calibarion is done"
                            )
                            calibrationIsDoneLiveData.postValue(true)
                        }
                        CalibrationState.MEDIUM -> mediumAngle = angle
                        CalibrationState.HIGH -> highAngle = angle
                    }
                    labelLiveData.postValue(step to angle)
                }
            } else {
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.calibration_labels_error))
            }
        }

    }

    fun clearData() {
        offAngle = null
        lowAngle = null
        mediumAngle = null
        highAngle = null
        calibrationIsDoneLiveData.value = false
    }

    private fun validateAngle(angle: Float, calibrationState: CalibrationState): Boolean {
        when (calibrationState) {
            CalibrationState.OFF -> {}
            CalibrationState.HIGH -> {}
            CalibrationState.MEDIUM -> {}
            CalibrationState.LOW -> {
                if (highAngle != null && mediumAngle != null) {
                    if(highAngle!!>mediumAngle!!){
                        if (angle in mediumAngle!!..highAngle!!){
                            return false
                        }
                    } else {
                        if (angle in highAngle!!..mediumAngle!!){
                            return false
                        }
                    }
                }
            }
        }
        offAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        lowAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        mediumAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        highAngle?.let { if (Math.abs(angle - it) < angleOffset) return false }
        return true
    }

    fun nextStep() {
        val currentIndex = calibrationStatesSequence.indexOf(currentCalibrationStateLiveData.value)
        if (currentIndex == calibrationStatesSequence.size - 1) {
            calibrationIsDoneLiveData.postValue(true)
        } else {
            currentCalibrationStateLiveData.postValue(calibrationStatesSequence[currentIndex + 1])
        }
    }

    fun previousStep() {
        val currentIndex = calibrationStatesSequence.indexOf(currentCalibrationStateLiveData.value)
        if (currentIndex == 0) {
            previousScreenTriggered.postValue(true)
        } else {
            val step = calibrationStatesSequence[currentIndex - 1]
            when (step) {
                CalibrationState.OFF -> offAngle = null
                CalibrationState.LOW -> lowAngle = null
                CalibrationState.MEDIUM -> mediumAngle = null
                CalibrationState.HIGH -> highAngle = null
            }
            currentCalibrationStateLiveData.postValue(step)
        }
    }
}

enum class CalibrationState {
    OFF, HIGH, MEDIUM, LOW
}

enum class CalibrationStep {
    CALIBRATION, CONFIRMATION, ROTATE_ITSELF
}
