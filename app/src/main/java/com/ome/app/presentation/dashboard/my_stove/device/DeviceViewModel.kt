package com.ome.app.presentation.dashboard.my_stove.device


import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.local.PreferencesProvider
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
    private val webSocketManager: WebSocketManager,
    private val savedStateHandle: SavedStateHandle,
    val pref: PreferencesProvider
) : BaseViewModel() {
    var stovePosition: Int  = -1
    val isEnable = MutableStateFlow(false)

    val currentKnob = savedStateHandle.getStateFlow("currentKnob", null as KnobDto?)

    val knobAngle = MutableStateFlow<Float?>(null)
    var macAddress = ""
    var isDualZone = false

    val deviceSettingsList by lazy {
        savedStateHandle.getStateFlow("deviceSettingsList",
            buildList {
                add(SettingsTitleItemModel(title = "Settings"))
                if(!isDualZone)
                    addAll(DeviceSettingsItemModel.entries.toList())
                else
                    addAll(DeviceSettingsItemModel.entries.toMutableList().apply { remove(DeviceSettingsItemModel.KnobOrientation) })
            }
        )
    }


    fun initSubscriptions() {
        launch(ioContext, showLoading = false) {
            webSocketManager.knobAngleFlow.filter { it?.macAddr == macAddress }.collect {
                it?.let {
                    logi("angle ViewModel ${it.value.toFloat()}")
                    knobAngle.value = it.value.toFloat()
                }
            }
        }
        launch(ioContext, showLoading = false) {
            stoveRepository.knobsFlow.mapNotNull  { dto -> dto.find { it.macAddr == macAddress }}.collect { foundKnob ->
                savedStateHandle["currentKnob"] = foundKnob
                if (webSocketManager.knobAngleFlow.value == null) {
                    knobAngle.value = foundKnob.angle.toFloat()
                }
            }
        }
    }

    fun changeKnobAngle(angle: Float) {
        launch(ioContext, showLoading = false) {
            stoveRepository.changeKnobAngle(
                params = ChangeKnobAngle(angle.toInt()),
                macAddress
            )
        }
    }

    fun deleteKnob(onEnd: () -> Unit) {
        launch(ioContext) {
            stoveRepository.deleteKnob(macAddress)
            stoveRepository.getAllKnobs()
            onEnd()
        }
    }

    fun startTurnOffTimer(hour: Int, minute: Int, second: Int){
        val totalSeconds = hour * 3600 + minute * 60 + second
        val offAngle = currentKnob.value?.calibration?.offAngle
        launch(ioContext) {
            offAngle?.also {
                stoveRepository.startTurnOffTimer(
                    macAddress = macAddress,
                    offAngle = offAngle,
                    second = totalSeconds
                )
//                pref.lastTimer = mapOf(macAddress to System.currentTimeMillis() + totalSeconds * 1000)
                pref.setTimer(macAddress, System.currentTimeMillis() )
            } ?: error("Something went wrong.")
        }
    }

    fun stopTimer() {
        launch(ioContext) {
            stoveRepository.stopTimer(macAddress)
//            pref.lastTimer = mapOf(macAddress to 0)
            pref.setTimer(macAddress,0)
        }
    }

}

