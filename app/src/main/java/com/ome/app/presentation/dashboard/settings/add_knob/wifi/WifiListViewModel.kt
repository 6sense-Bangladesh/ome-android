package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.data.local.SocketManager
import com.ome.app.utils.WifiHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WifiListViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager
) : BaseViewModel() {

    var macAddr = ""

    val wifiNetworksListLiveData: SingleLiveEvent<List<NetworkItemModel>> = SingleLiveEvent()


    init {
        launch(ioContext) {
            socketManager.networksFlow.collect { list ->
                if (list.isNotEmpty()) {
                    wifiNetworksListLiveData.postValue(list)
                }
            }
        }
    }

    fun sendMessage(message: KnobSocketMessage) = launch(ioContext) {
        socketManager.sendMessage(message)
    }
}
