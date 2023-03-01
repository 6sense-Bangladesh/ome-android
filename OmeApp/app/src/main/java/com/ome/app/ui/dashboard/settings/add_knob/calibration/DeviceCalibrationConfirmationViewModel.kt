package com.ome.app.ui.dashboard.settings.add_knob.calibration

import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.model.network.request.ChangeKnobAngle
import com.ome.app.model.network.request.SetCalibrationRequest
import com.ome.app.model.network.request.Zone
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

    var labelLiveData = SingleLiveEvent<Pair<CalibrationState, Float>>()

    val calibrationIsDoneLiveData = SingleLiveEvent<Boolean>()
    val previousScreenTriggered = SingleLiveEvent<Boolean>()


    fun nextStep() = launch(dispatcher = ioContext) {
        when (currentCalibrationStateLiveData.value) {
            CalibrationState.OFF -> {
                setCalibration()
                calibrationIsDoneLiveData.postValue(true)
            }
            CalibrationState.HIGH -> {
                mediumAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.MEDIUM)
            }
            CalibrationState.MEDIUM -> {
                lowAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.LOW)
            }
            CalibrationState.LOW -> {
                offAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.OFF)
            }
            else -> {
                highAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.HIGH)
            }
        }
    }

    private fun setCalibration() = launch(dispatcher = ioContext) {
        if (!isDualKnob) {
            if (offAngle != null && lowAngle != null && mediumAngle != null && highAngle != null && rotationDir != null) {
                stoveRepository.setCalibration(
                    SetCalibrationRequest(
                        offAngle = offAngle!!.toInt(),
                        rotationDir = rotationDir!!,
                        zones = arrayListOf(
                            Zone(
                                highAngle = highAngle!!.toInt(),
                                mediumAngle = mediumAngle!!.toInt(),
                                lowAngle = lowAngle!!.toInt(),
                                zoneName = "Single",
                                zoneNumber = 1
                            )
                        )
                    ),
                    macAddress
                )
            }
        }
    }

    fun previousStep() = launch(dispatcher = ioContext) {
        when (currentCalibrationStateLiveData.value) {
            CalibrationState.OFF -> {
                lowAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.LOW)
            }
            CalibrationState.HIGH -> {
                currentCalibrationStateLiveData.postValue(null)
                firstConfirmationPageLiveData.postValue(true)
            }
            CalibrationState.MEDIUM -> {
                highAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.HIGH)
            }
            CalibrationState.LOW -> {
                mediumAngle?.let {
                    stoveRepository.changeKnobAngle(
                        params = ChangeKnobAngle(it.toInt()),
                        macAddress
                    )
                }
                currentCalibrationStateLiveData.postValue(CalibrationState.MEDIUM)
            }
            else -> {
                previousScreenTriggered.postValue(true)
            }
        }
    }
}
