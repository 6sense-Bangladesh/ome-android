package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.data.local.SocketManager
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.WifiHandler
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class ManualSetupViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    private val connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    suspend fun isConnectedToKnobHotspot(): Boolean = wifiHandler.isConnectedToKnobHotspot()

    var macAddr = ""

    val wifiConnectedFlow = MutableSharedFlow<Unit>()

    private var getListTryCount = 0

    init {
        connectionStatusListener.shouldReactOnChanges = false
        launch(ioContext) {
            socketManager.networksFlow.filterNotNull().collect { list ->
                list.log("wifiNetworksList")
                if (list.isNotEmpty() || getListTryCount > 0) {
                    wifiConnectedFlow.emit(Unit)
                }else{
                    getListTryCount++
                    delay(500)
                    getNetworks()
                }
            }
        }
    }

    fun connectToSocket() = launch(ioContext) {
        socketManager.connect()
    }

    fun initListeners() = launch(ioContext) {
        socketManager.onSocketConnect = {
            getNetworks()
        }
        socketManager.messageReceived = { type, message ->

        }
    }

    private fun getNetworks() = launch(ioContext) {
        socketManager.sendMessage(KnobSocketMessage.GET_NETWORKS)
    }
}
