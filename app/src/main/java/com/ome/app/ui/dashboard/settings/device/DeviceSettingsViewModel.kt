package com.ome.app.ui.dashboard.settings.device

import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.model.network.response.KnobDto
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DeviceSettingsViewModel @Inject constructor(
    val userRepository: UserRepository,
    val stoveRepository: StoveRepository,
    val webSocketManager: WebSocketManager) : BaseViewModel() {

    val knobAngleLiveData = SingleLiveEvent<Float?>()
    val zonesLiveData = SingleLiveEvent<KnobDto.CalibrationDto>()
    var macAddress = ""

    var offAngle: Float? = null
    var lowSingleAngle: Float? = null
    var mediumAngle: Float? = null
    var highSingleAngle: Float? = null
    var lowDualAngle: Float? = null
    var highDualAngle: Float? = null

    fun initSubscriptions() {
        launch(dispatcher = ioContext) {
            webSocketManager.knobAngleFlow.collect {
                it?.let {
                    logi("angle ViewModel ${it.value.toFloat()}")
                    knobAngleLiveData.postValue(it.value.toFloat())
                }
            }
        }
        launch(dispatcher = ioContext) {
            stoveRepository.knobsFlow.collect { knobs ->
                knobs?.let {
                    val foundKnob = knobs.firstOrNull { it.macAddr == macAddress }
                    foundKnob?.let {
                        zonesLiveData.postValue(foundKnob.calibration)
                        if (webSocketManager.knobAngleFlow.value == null) {
                            knobAngleLiveData.postValue(it.angle.toFloat())
                        }
                    }
                }
            }
        }
    }

}

