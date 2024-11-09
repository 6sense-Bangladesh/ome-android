package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.SocketManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.IO
import com.ome.app.utils.WifiHandler
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class ConnectToWifiViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    val stoveRepository: StoveRepository,
    private val connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    var macAddrs = ""
    var isChangeWifiMode = false

    val wifiConnectedFlow = MutableSharedFlow<Unit>()

    private var getListTryCount = 0

    init {
        launch(ioContext) {
            socketManager.networksFlow.filterNotNull().collect { list ->
                list.log("wifiNetworksList")
                if (list.isNotEmpty() || getListTryCount > 0) {
                    wifiConnectedFlow.emit(Unit)
                }else{
                    getListTryCount++
                    delay(500)
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                }
            }
        }
    }


    fun setupWifi() {
        if (macAddrs.isNotEmpty()) {
            wifiHandler.setup(macAddrs)
        }
    }

    private fun connectToSocket() {
        launch(ioContext) {
            socketManager.connect()
        }
    }

    fun initListeners() = launch(ioContext) {
        socketManager.messageReceived = { type, message ->
            if (type == KnobSocketMessageType.GET_MAC) {
                if (message == macAddrs) {
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                }
            }
        }
        socketManager.onSocketConnect = {
            sendMessage(KnobSocketMessageType.GET_MAC)
        }
    }

    private fun sendMessage(message: KnobSocketMessageType) = launch(ioContext) {
        socketManager.sendMessage(message)
    }


    fun connectToWifi(){
        launch {
            if(isChangeWifiMode){
                IO{ stoveRepository.clearWifi(macAddrs) }
                delay(6.seconds)
            }
            connectionStatusListener.shouldReactOnChanges = false
            val result = wifiHandler.connectToWifi()

            //Check whether device connected to wifi or not
            if (result.first) {
                connectToSocket()
            } else {
                result.second?.let { message ->
                    loadingLiveData.postValue(false)
                    defaultErrorLiveData.postValue(message)
                } ?: run {
                    connectToWifi()
                }
            }
        }
    }
}
