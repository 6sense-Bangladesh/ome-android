package com.ome.app.ui.dashboard.settings.add_knob.wifi

import com.ome.Ome.R
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.data.local.KnobSocketMessage
import com.ome.app.data.local.SocketManager
import com.ome.app.utils.WifiHandler
import com.ome.app.utils.withDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectToWifiPasswordViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    val webSocketManager: WebSocketManager,
    val preferencesProvider: PreferencesProvider,
    val resourceProvider: ResourceProvider,
    val stoveRepository: StoveRepository
) : BaseViewModel() {

    var macAddr = ""
    var ssid = ""
    var securityType = ""
    var password = ""

    val wifiConnectedLiveData: SingleLiveEvent<Pair<Boolean, String?>> = SingleLiveEvent()
    val networkDisconnectStatusLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()


    fun initListeners() {
        socketManager.messageReceived = { type, message ->
            when (type) {
                KnobSocketMessage.TEST_WIFI -> {
                    if (message == "ok") {
                        sendMessage(KnobSocketMessage.WIFI_STATUS)
                    }
                }
                KnobSocketMessage.WIFI_STATUS -> {
                    handleWifiStatusMessage(message)
                }
                KnobSocketMessage.SET_WIFI -> {
                    if (message == "ok") {
                        sendMessage(KnobSocketMessage.REBOOT)
                        withDelay(2000) {
                            disconnectFromNetwork()
                        }
                        withDelay(3000) {
                            successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
                            loadingLiveData.postValue(false)
                        }
                    } else {
                        defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
                    }
                }
                else -> {

                }
            }
        }


        socketManager.onSocketConnect = {
        }
    }

    private fun disconnectFromNetwork() = launch(dispatcher = ioContext) {
        val response = wifiHandler.disconnectFromNetwork()
        withDelay(3000) {
            connectionStatusListener.shouldReactOnChanges = true
            networkDisconnectStatusLiveData.postValue(response)
        }
    }

    private fun handleWifiStatusMessage(message: String) {
        when (message) {
            "0", "3" -> {
                sendMessage(
                    KnobSocketMessage.SET_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
            "4", "6" -> {
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
            }
            "2" -> {
                withDelay(delay = 4000) {
                    sendMessage(KnobSocketMessage.WIFI_STATUS)
                }
            }
            "1" -> {
                sendMessage(
                    KnobSocketMessage.TEST_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
            "5" -> {
                sendMessage(
                    KnobSocketMessage.SET_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
        }

    }

    fun sendMessage(
        message: KnobSocketMessage,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = launch(dispatcher = ioContext) {
        socketManager.sendMessage(message, ssid, password, securityType)
    }

}
