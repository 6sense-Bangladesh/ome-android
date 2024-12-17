package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.ChangeKnobAngle
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceCalibrationConfirmationViewModel @Inject constructor(
    webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseCalibrationViewModel(webSocketManager, stoveRepository) {


//    var currentCalibrationStateLiveData = SingleLiveEvent<CalibrationState?>().apply { postValue(null) }
    var currentCalibrationState = MutableStateFlow<CalibrationState?>(null)

    private var firstConfirmationPageLiveData = SingleLiveEvent<Boolean>()


    val previousScreenTriggered = SingleLiveEvent<Boolean>()


    private var offTriggerCount = 0
    private var currentStepTriggerCount = 0


    override fun handleDualKnobUpdated(angle: Float) {
        knobAngleFlow.value = angle
    }

    fun triggerCurrentStepAgain() = launch(ioContext) {
        if (currentStepTriggerCount < 1) {
            when (currentCalibrationState.value) {
                CalibrationState.OFF -> {
                    offAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }
                CalibrationState.HIGH_SINGLE -> {
                    highSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }

                CalibrationState.HIGH_DUAL -> {
                    highDualAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }

                CalibrationState.LOW_DUAL -> {
                    lowDualAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }
                CalibrationState.MEDIUM -> {
                    mediumAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }
                CalibrationState.LOW_SINGLE -> {
                    lowSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                }
                else -> {

                }
            }
            currentStepTriggerCount++
        } else {
            error(resourceProvider.getString(R.string.issue_with_knob_label))
        }

    }

    fun nextStep() = launch(ioContext) {

        if (!isDualKnob) {
            currentCalibrationState.value.log("nextStep")
//            when (currentCalibrationState.value) {
//                CalibrationState.OFF -> {
//                    setCalibration()
//                }
//                CalibrationState.MOVE_OFF -> {
//
//                }
//                CalibrationState.HIGH_SINGLE -> {
//                    mediumAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.MEDIUM
//                }
//                CalibrationState.MEDIUM -> {
//                    lowSingleAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.LOW_SINGLE
//                }
//                CalibrationState.LOW_SINGLE -> {
//                    currentCalibrationState.value = CalibrationState.OFF
//                    offAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                }
//                null -> {
//                    highSingleAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.HIGH_SINGLE
//                }
//                CalibrationState.HIGH_DUAL, CalibrationState.LOW_DUAL -> Unit
//            }
            if(currentCalibrationState.value == CalibrationState.OFF)
                setCalibration()
            else{
                currentCalibrationState.value = calibrationConfirmationStatesSingleZone.run {
                    getOrNull(indexOfFirst{ it.first == currentCalibrationState.value } + 1)?.apply {
                        if(second != null)
                            stoveRepository.changeKnobAngle(params = ChangeKnobAngle(second!!.toInt()), macAddress)
                    }?.first
                }
            }
        } else {
//            when (currentCalibrationState.value) {
//                CalibrationState.OFF -> {
//                    setCalibration()
//                    calibrationIsDoneLiveData.postValue(true)
////                    if (offTriggerCount > 1) {
////                        setCalibration()
////                        calibrationIsDoneLiveData.postValue(true)
////                    } else {
////                        highSingleAngle?.let {
////                            stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
////                        }
////                        currentCalibrationState.value = CalibrationState.HIGH_SINGLE
////                    }
//                }
//                CalibrationState.MOVE_OFF -> {
//
//                }
//                null -> {
//                    highSingleAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.HIGH_SINGLE
//                }
//                CalibrationState.HIGH_SINGLE -> {
//                    lowSingleAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
////                    offTriggerCount++
//                }
//                CalibrationState.LOW_SINGLE -> {
//                    highDualAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.HIGH_DUAL
//                }
//                CalibrationState.HIGH_DUAL -> {
//                    lowDualAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    currentCalibrationState.value = CalibrationState.LOW_DUAL
//                }
//                CalibrationState.LOW_DUAL -> {
//                    offAngle?.let {
//                        stoveRepository.changeKnobAngle(params = ChangeKnobAngle(it.toInt()), macAddress)
//                    }
//                    offTriggerCount++
//                    currentCalibrationState.value = CalibrationState.OFF
//                }
//                CalibrationState.MEDIUM -> Unit
//            }
            if(currentCalibrationState.value == CalibrationState.OFF)
                setCalibration()
            else{
                currentCalibrationState.value = calibrationConfirmationStatesDualZone.run {
                    getOrNull(indexOfFirst{ it.first == currentCalibrationState.value } + 1)?.apply {
                        if(first != CalibrationState.LOW_DUAL && second != null)
                            stoveRepository.changeKnobAngle(params = ChangeKnobAngle(second!!.toInt()), macAddress)
                    }?.first
                }
            }

        }
//        currentStepTriggerCount = 0
    }


    fun previousStep() = launch(ioContext) {
        when (currentCalibrationState.value) {
            CalibrationState.OFF -> {
                if (!isDualKnob) {
                    lowSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationState.value = CalibrationState.LOW_SINGLE
                } else {
                    currentCalibrationState.value = null
                    firstConfirmationPageLiveData.postValue(true)
                }
            }
            CalibrationState.HIGH_SINGLE -> {
                if (!isDualKnob) {
                    currentCalibrationState.value = null
                    firstConfirmationPageLiveData.postValue(true)
                } else {
                    currentCalibrationState.value = null
                    firstConfirmationPageLiveData.postValue(true)
                }
            }
            CalibrationState.MEDIUM -> {
                highSingleAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationState.value = CalibrationState.HIGH_SINGLE
            }
            CalibrationState.LOW_DUAL -> {
                currentCalibrationState.value = null
                firstConfirmationPageLiveData.postValue(true)

            }
            CalibrationState.HIGH_DUAL -> {
                currentCalibrationState.value = null
                firstConfirmationPageLiveData.postValue(true)
            }
            CalibrationState.LOW_SINGLE -> {
                if (!isDualKnob) {
                    mediumAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationState.value = CalibrationState.MEDIUM
                } else {
                    currentCalibrationState.value = null
                    firstConfirmationPageLiveData.postValue(true)

                }
            }
            else -> {
                previousScreenTriggered.postValue(true)
            }
        }
    }
}
