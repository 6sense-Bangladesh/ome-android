package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.NetworkManager
import com.ome.app.data.local.SocketManager
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ManualSetupViewModel @Inject constructor(
    val networkManager: NetworkManager,
    private val socketManager: SocketManager,
    connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    var macAddr = ""
    private var socketConnected = false

    val wifiConnectedFlow = MutableSharedFlow<Boolean>()

    private var getListTryCount = 0

    init {
        connectionStatusListener.shouldReactOnChanges = false
        launch(ioContext) {
            socketManager.networksFlow.filterNotNull().collect { list ->
                list.log("wifiNetworksList")
                if (list.isNotEmpty() || getListTryCount > 1) {
                    wifiConnectedFlow.emit(true)
                }else{
                    getListTryCount++
                    delay(500)
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                }
            }
        }
    }

    fun connectToSocket(){
        launch {
            delay(1.minutes)
            if(!socketConnected)
                wifiConnectedFlow.emit(false)
        }
        launch(ioContext) {
            delay(5.seconds)
            socketManager.connect()
        }
    }

    fun initListeners() = launch(ioContext) {
        socketManager.onSocketConnect = {
            socketConnected = it
            if(it) {
                networkManager.isConnected = true
                sendMessage(KnobSocketMessageType.GET_MAC)
            }
            else
                wifiConnectedFlow.tryEmit(false)
        }
        socketManager.messageReceived = { type, message ->
            if (type == KnobSocketMessageType.GET_MAC) {
                if (message == macAddr)
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                else
                    wifiConnectedFlow.tryEmit(false)
            }
        }
    }

    private fun sendMessage(message: KnobSocketMessageType) = launch(ioContext) {
        socketManager.sendMessage(message)
    }
}
