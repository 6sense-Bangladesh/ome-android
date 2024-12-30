package com.ome.app.presentation.dashboard.my_stove.device


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.TAG
import com.ome.app.domain.model.network.request.ChangeKnobAngle
import com.ome.app.domain.model.network.request.KnobRequest
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsTitleItemModel
import com.ome.app.utils.log
import com.ome.app.utils.toTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    val initAngle = MutableStateFlow<Int?>(null)
    val showTimer = MutableSharedFlow<Unit>()
    var isSafetyLockOn = false

    val currentKnob = savedStateHandle.getStateFlow("currentKnob", null as KnobDto?)

    val knobAngle = MutableStateFlow<Float?>(null)
    var macAddress = ""
    var isDualZone = false

    val isPauseEnabled
        get() = pref.getPauseTime(macAddress).let { (hr, min, sec) ->
            Log.d(TAG, "isPauseEnabled: $hr $min $sec")
            hr + min + sec != 0
        }

    val deviceSettingsList by lazy {
        savedStateHandle.getStateFlow("deviceSettingsList",
            buildList {
                add(SettingsTitleItemModel(title = "Settings"))
                addAll(DeviceSettingsItemModel.entries.toList())
            }
        )
    }


    fun initSubscriptions() {
        launch(showLoading = false) {
            webSocketManager.knobAngleFlow.filter { it?.macAddr == macAddress }.collect {
                it?.let {
                    log("angle ViewModel ${it.value.toFloat()}")
                    knobAngle.value = it.value.toFloat()
                }
            }
        }
        launch(showLoading = false) {
            stoveRepository.knobsFlow.mapNotNull  { dto -> dto.find { it.macAddr == macAddress }}.collect { foundKnob ->
                savedStateHandle["currentKnob"] = foundKnob
                isSafetyLockOn = foundKnob.safetyLock
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

    fun deleteKnob() {
        launch(ioContext) {
            stoveRepository.updateKnobInfo(
                params = KnobRequest(calibrated = false),
                macAddress = macAddress
            )
            stoveRepository.setSafetyLockOff(macAddress)
            stoveRepository.deleteKnob(macAddress)
            stoveRepository.knobsFlow.value = stoveRepository.knobsFlow.value.filter { it.macAddr != macAddress }
            webSocketManager.knobState .value = webSocketManager.knobState.value.filter { it.key != macAddress }
        }
    }

    fun startTurnOffTimer(hour: Int, minute: Int, second: Int){
        val totalSeconds = hour * 3600 + minute * 60 + second
        val offAngle = currentKnob.value?.calibration?.offAngle
        launch(ioContext) {
            offAngle?.also {
                stoveRepository.startTurnOffTimer(
                    macAddress = macAddress,
                    currentAngle = knobAngle.value?.toInt() ?: offAngle,
                    offAngle = offAngle,
                    second = totalSeconds
                )
                pref.setTimer(macAddress, System.currentTimeMillis() + totalSeconds * 1000)
                showTimer.emit(Unit)
            } ?: error("Something went wrong.")
        }
    }

    fun stopTimer(onlyLocal: Boolean = false) {
        launch(ioContext) {
            if(!onlyLocal)
                stoveRepository.stopTimer(macAddress)
            pref.setTimer(macAddress,0)
        }
    }

    fun pauseTimer(time: Int?) {
        launch(ioContext, showLoading = false) {
            stopTimer(time == null)
            pref.setPauseTime(macAddress, time?.toTimer())
        }
    }
    fun resumeTimer() {
        launch(ioContext) {
            val time = pref.getPauseTime(macAddress)
            pauseTimer(null)
            startTurnOffTimer(time.first, time.second, time.third)
        }
    }

}

