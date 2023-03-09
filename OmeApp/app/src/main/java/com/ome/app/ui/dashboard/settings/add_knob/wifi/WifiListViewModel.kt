package com.ome.app.ui.dashboard.settings.add_knob.wifi

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
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
        launch(dispatcher = ioContext) {
            socketManager.networksFlow.collect { list ->
                if (list.isNotEmpty()) {
                    wifiNetworksListLiveData.postValue(list)
                }
            }
        }
    }

    fun sendMessage(message: KnobSocketMessage) = launch(dispatcher = ioContext) {
        socketManager.sendMessage(message)
    }
}
