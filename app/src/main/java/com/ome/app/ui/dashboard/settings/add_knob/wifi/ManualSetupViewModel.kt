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
class ManualSetupViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager
) : BaseViewModel() {

    fun isConnectedToKnobHotspot(): Boolean = wifiHandler.isConnectedToKnobHotspot()

    val wifiNetworksListLiveData: SingleLiveEvent<List<NetworkItemModel>> = SingleLiveEvent()

    var macAddr = ""

    init {
        connectionStatusListener.shouldReactOnChanges = false
        launch(dispatcher = ioContext) {
            socketManager.networksFlow.collect { list->
                if(list.isNotEmpty()){
                    wifiNetworksListLiveData.postValue(list)
                }
            }
        }
    }

    fun connectToSocket() = launch(dispatcher = ioContext) {
        socketManager.connect()
    }

    fun initListeners() = launch(dispatcher = ioContext) {
        socketManager.onSocketConnect = {
            getNetworks()
        }
        socketManager.messageReceived = { type, message ->

        }
    }

    private fun getNetworks() = launch(dispatcher = ioContext) {
        socketManager.sendMessage(KnobSocketMessage.GET_NETWORKS)
    }
}
