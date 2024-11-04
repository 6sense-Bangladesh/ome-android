package com.ome.app.presentation.dashboard.settings.add_knob.calibration

import com.ome.app.R
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.ChangeKnobAngle
import com.ome.app.domain.model.network.request.SetCalibrationRequest
import com.ome.app.domain.model.network.request.Zone
import com.ome.app.utils.KnobAngleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceCalibrationConfirmationViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseCalibrationViewModel(webSocketManager, stoveRepository, resourceProvider) {


    var currentCalibrationStateLiveData =
        SingleLiveEvent<CalibrationState?>().apply { postValue(null) }

    var firstConfirmationPageLiveData = SingleLiveEvent<Boolean>()


    val calibrationIsDoneLiveData = SingleLiveEvent<Boolean>()
    val previousScreenTriggered = SingleLiveEvent<Boolean>()


    var offTriggerCount = 0
    var currentStepTriggerCount = 0


    override fun handleDualKnobUpdated(value: Float) {
        knobAngleLiveData.postValue(value)
    }

    fun triggerCurrentStepAgain() = launch(ioContext) {
        if (currentStepTriggerCount < 1) {
            when (currentCalibrationStateLiveData.value) {
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
            defaultErrorLiveData.postValue(resourceProvider.getString(R.string.issue_with_knob_label))
        }

    }

    fun nextStep() = launch(ioContext) {
        when (currentCalibrationStateLiveData.value) {
            CalibrationState.OFF -> {
                if (!isDualKnob) {
                    setCalibration()
                    calibrationIsDoneLiveData.postValue(true)
                } else {
                    if (offTriggerCount > 1) {
                        setCalibration()
                        calibrationIsDoneLiveData.postValue(true)
                    } else {
                        highSingleAngle?.let {
                            stoveRepository.changeKnobAngle(
                                params = ChangeKnobAngle(it.toInt()),
                                macAddress
                            )
                        }
                        currentCalibrationStateLiveData.postValue(CalibrationState.HIGH_SINGLE)
                    }
                }
            }
            CalibrationState.HIGH_SINGLE -> {
                if (!isDualKnob) {
                    mediumAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.MEDIUM)
                } else {
                    offAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    offTriggerCount++
                    currentCalibrationStateLiveData.postValue(CalibrationState.OFF)
                }

            }

            CalibrationState.HIGH_DUAL -> {
                lowDualAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.LOW_DUAL)
            }

            CalibrationState.LOW_DUAL -> {
                offAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                offTriggerCount++
                currentCalibrationStateLiveData.postValue(CalibrationState.OFF)
            }
            CalibrationState.MEDIUM -> {
                lowSingleAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.LOW_SINGLE)
            }
            CalibrationState.LOW_SINGLE -> {
                if (!isDualKnob) {
                    offAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.OFF)
                } else {
                    highSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.HIGH_SINGLE)
                }
            }
            else -> {
                if (!isDualKnob) {
                    highSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.HIGH_SINGLE)
                } else {
                    highDualAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.HIGH_DUAL)
                }
            }
        }
        currentStepTriggerCount = 0
    }

    private fun setCalibration() = launch(ioContext) {
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
        } else {
            if (offAngle != null
                && lowSingleAngle != null
                && highSingleAngle != null
                && lowDualAngle != null
                && highDualAngle != null
                && rotationDir != null
            ) {
                stoveRepository.setCalibration(
                    SetCalibrationRequest(
                        offAngle = offAngle!!.toInt(),
                        rotationDir = 2,
                        zones = arrayListOf(
                            Zone(
                                highAngle = highSingleAngle!!.toInt(),
                                mediumAngle = KnobAngleManager.generateMediumAngle(
                                    highSingleAngle!!.toInt(),
                                    lowSingleAngle!!.toInt()
                                ),
                                lowAngle = lowSingleAngle!!.toInt(),
                                zoneName = "Single",
                                zoneNumber = 1
                            ),
                            Zone(
                                highAngle = highDualAngle!!.toInt(),
                                mediumAngle = KnobAngleManager.generateMediumAngle(
                                    highDualAngle!!.toInt(),
                                    lowDualAngle!!.toInt()
                                ),
                                lowAngle = lowDualAngle!!.toInt(),
                                zoneName = "Dual",
                                zoneNumber = 2
                            )
                        )
                    ),
                    macAddress
                )
            }
        }

        stoveRepository.getAllKnobs()
    }

    fun previousStep() = launch(ioContext) {
        when (currentCalibrationStateLiveData.value) {
            CalibrationState.OFF -> {
                if (!isDualKnob) {
                    lowSingleAngle?.let {
                        stoveRepository.changeKnobAngle(
                            params = ChangeKnobAngle(it.toInt()),
                            macAddress
                        )
                    }
                    currentCalibrationStateLiveData.postValue(CalibrationState.LOW_SINGLE)
                } else {
                    currentCalibrationStateLiveData.postValue(null)
                    firstConfirmationPageLiveData.postValue(true)
                }
            }
            CalibrationState.HIGH_SINGLE -> {
                if (!isDualKnob) {
                    currentCalibrationStateLiveData.postValue(null)
                    firstConfirmationPageLiveData.postValue(true)
                } else {
                    currentCalibrationStateLiveData.postValue(null)
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
                currentCalibrationStateLiveData.postValue(CalibrationState.HIGH_SINGLE)
            }
            CalibrationState.LOW_DUAL -> {
                currentCalibrationStateLiveData.postValue(null)
                firstConfirmationPageLiveData.postValue(true)

            }
            CalibrationState.HIGH_DUAL -> {
                currentCalibrationStateLiveData.postValue(null)
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
                    currentCalibrationStateLiveData.postValue(CalibrationState.MEDIUM)
                } else {
                    currentCalibrationStateLiveData.postValue(null)
                    firstConfirmationPageLiveData.postValue(true)

                }
            }
            else -> {
                previousScreenTriggered.postValue(true)
            }
        }
    }
}
