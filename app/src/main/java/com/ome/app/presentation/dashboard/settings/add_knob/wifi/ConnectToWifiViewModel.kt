package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import androidx.lifecycle.SavedStateHandle
import com.ome.app.BuildConfig
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.data.local.SocketManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import com.ome.app.utils.WifiHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ConnectToWifiViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    val stoveRepository: StoveRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var macAddrs = ""
    var isChangeWifiMode = false

    val wifiConnectedLiveData: SingleLiveEvent<Pair<Boolean, String?>> = SingleLiveEvent()
    val wifiNetworksList = savedStateHandle.getStateFlow<List<NetworkItemModel>?>("wifiNetworksList",
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
            socketManager.networksFlow.collect { list ->
                if (list.isNotEmpty()) {
                    savedStateHandle["wifiNetworksList"] = list
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
            if (type == KnobSocketMessage.GET_MAC) {
                if (message == macAddrs) {
                    sendMessage(KnobSocketMessage.GET_NETWORKS)
                }
            }
        }
        socketManager.onSocketConnect = {
            sendMessage(KnobSocketMessage.GET_MAC)
        }
    }

    private fun sendMessage(message: KnobSocketMessage) = launch(ioContext) {
        socketManager.sendMessage(message)
    }


    fun connectToWifi() = launch {
        if(isChangeWifiMode){
            withContext(Dispatchers.IO){ stoveRepository.clearWifi(macAddrs) }
//            delay(6000)
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
            }
        }
    }
}
