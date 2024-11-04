package com.ome.app.ui.dashboard.my_stove.device


import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.ChangeKnobAngle
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsTitleItemModel
import com.ome.app.utils.isTrue
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import javax.inject.Inject


@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository,
    val webSocketManager: WebSocketManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val knobAngleLiveData = SingleLiveEvent<Float?>()
    val zonesLiveData = SingleLiveEvent<KnobDto.CalibrationDto>()
    var macAddress = ""

    var offAngle: Float? = null
    var lowSingleAngle: Float? = null
    var mediumAngle: Float? = null
    var highSingleAngle: Float? = null
    var lowDualAngle: Float? = null
    var highDualAngle: Float? = null

    val deviceSettingsList = savedStateHandle.getStateFlow("deviceSettingsList",
        buildList {
            add(SettingsTitleItemModel(title = "Settings"))
            addAll(DeviceSettingsItemModel.entries.toList())
        }
    )


    fun initSubscriptions() {
        launch(ioContext) {
            webSocketManager.knobAngleFlow.filter { it?.macAddr == macAddress }.collect {
                it?.let {
                    logi("angle ViewModel ${it.value.toFloat()}")
                    knobAngleLiveData.postValue(it.value.toFloat())
                }
            }
        }
        launch(ioContext) {
            stoveRepository.knobsFlow.collect { knobs ->
                knobs.let {
                    val foundKnob = knobs.firstOrNull { it.macAddr == macAddress }
                    foundKnob?.let {
                        foundKnob.calibrated.isTrue{
                            zonesLiveData.postValue(foundKnob.calibration)
                        }
                        if (webSocketManager.knobAngleFlow.value == null) {
                            knobAngleLiveData.postValue(it.angle.toFloat())
                        }
                    }
                }
            }
        }
    }

    fun changeKnobAngle(angle: Float) {
        launch {
            stoveRepository.changeKnobAngle(
                params = ChangeKnobAngle(angle.toInt()),
                macAddress
            )
        }
    }

    fun deleteKnob(onEnd: () -> Unit) {
        launch {
            stoveRepository.deleteKnob(macAddress)
            onEnd()
        }
    }

}

