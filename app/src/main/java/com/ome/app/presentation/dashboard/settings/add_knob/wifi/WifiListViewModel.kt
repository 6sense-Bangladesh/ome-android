package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import androidx.lifecycle.SavedStateHandle
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.SocketManager
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WifiListViewModel @Inject constructor(
    val socketManager: SocketManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var macAddr = ""

    val wifiNetworksList = savedStateHandle.getStateFlow("wifiNetworksList", listOf<NetworkItemModel>())


    init {
        launch(ioContext) {
            socketManager.networksFlow.collect { list ->
                savedStateHandle["wifiNetworksList"] = list.orEmpty()
            }
        }
    }

    fun getNetworks() = launch(ioContext) {
        socketManager.sendMessage(KnobSocketMessageType.GET_NETWORKS)
    }
}
