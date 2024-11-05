package com.ome.app.presentation.dashboard.my_stove.device


import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.request.ChangeKnobAngle
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsTitleItemModel
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject


@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val stoveRepository: StoveRepository,
    val webSocketManager: WebSocketManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var stovePosition: Int  = -1
    val isEnable = MutableStateFlow(false)

    val currentKnob = savedStateHandle.getStateFlow("currentKnob", null as KnobDto?)

    val knobAngle = MutableStateFlow<Float?>(null)
    var macAddress = ""

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
                    knobAngle.value = it.value.toFloat()
                }
            }
        }
        launch(ioContext) {
            stoveRepository.knobsFlow.mapNotNull  { dto -> dto.find { it.macAddr == macAddress }}.collect { foundKnob ->
                savedStateHandle["currentKnob"] = foundKnob
                if (webSocketManager.knobAngleFlow.value == null) {
                    knobAngle.value = foundKnob.angle.toFloat()
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

