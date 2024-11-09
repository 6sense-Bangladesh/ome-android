package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import androidx.lifecycle.SavedStateHandle
import com.ome.app.BuildConfig
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.SocketManager
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import com.ome.app.utils.WifiHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


@HiltViewModel
class WifiListViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var macAddr = ""

    val wifiNetworksList = savedStateHandle.getStateFlow<List<NetworkItemModel>>("wifiNetworksList",
        buildList {
            if(BuildConfig.IS_INTERNAL_TESTING) {
                addAll(listOf(
                    NetworkItemModel(ssid = "eloquentiam", securityType = "feugiat"),
                    NetworkItemModel(ssid = "sumo", securityType = "libero"),
                    NetworkItemModel(ssid = "vitae", securityType = "purus")
                ))
            }
        }
    )


    init {
        launch(ioContext) {
            socketManager.networksFlow.filterNotNull().collect { list ->
                if (list.isNotEmpty()) {
                    savedStateHandle["wifiNetworksList"] = list
                }
            }
        }
    }

    fun sendMessage(message: KnobSocketMessageType) = launch(ioContext) {
        socketManager.sendMessage(message)
    }
}
