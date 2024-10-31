package com.ome.app.ui.dashboard.my_stove.device


import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsTitleItemModel
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository,
    private val webSocketManager: WebSocketManager,
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
            webSocketManager.knobAngleFlow.collect {
                it?.let {
                    logi("angle ViewModel ${it.value.toFloat()}")
                    knobAngleLiveData.postValue(it.value.toFloat())
                }
            }
        }
        launch(ioContext) {
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

@Parcelize
data class DeviceFragmentParams(val stovePosition: Int, val macAddr: String) : Parcelable

