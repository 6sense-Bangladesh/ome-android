package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.local.SocketManager
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.MAIN
import com.ome.app.utils.WifiHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ConnectToWifiPasswordViewModel @Inject constructor(
    val wifiHandler: WifiHandler,
    val socketManager: SocketManager,
    val webSocketManager: WebSocketManager,
    val preferencesProvider: PreferencesProvider,
    val resourceProvider: ResourceProvider,
    val stoveRepository: StoveRepository,
    private val connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    var macAddr = ""
    var ssid = ""
    var securityType = ""
    var password = ""



    fun initListeners() {
        socketManager.messageReceived = { type, message ->
            when (type) {
                KnobSocketMessageType.TEST_WIFI -> {
                    if (message == "ok") {
                        sendMessage(KnobSocketMessageType.WIFI_STATUS)
                    }
                }
                KnobSocketMessageType.WIFI_STATUS -> {
                    handleWifiStatusMessage(message)
                }
                KnobSocketMessageType.SET_WIFI -> {
                    if (message == "ok") {
                        sendMessage(KnobSocketMessageType.REBOOT)
//                        withDelay(2000) {
//                            disconnectFromNetwork()
//                        }
//                        successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
//                        loadingLiveData.postValue(false)
                        disconnectFromNetwork()
//                        delay(1.seconds)
                        successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
                        loadingLiveData.postValue(false)
                    } else {
                        defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
                    }
                }
                else -> {

                }
            }
        }
//
//        socketManager.onSocketConnect = {
//        }
    }

    private suspend fun disconnectFromNetwork(){
        delay(2.seconds)
        MAIN { wifiHandler.disconnectFromNetwork() }
        connectionStatusListener.shouldReactOnChanges = true
    }

    private suspend fun handleWifiStatusMessage(message: String) {
        when (message) {
            "0", "3", "2", "5" -> {
                sendMessage(
                    KnobSocketMessageType.SET_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
            "4", "6" -> {
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
            }
//            "2" -> {
//                withDelay(delay = 4000) {
//                    sendMessage(KnobSocketMessage.WIFI_STATUS)
//                }
//            }
            "1" -> {
                sendMessage(
                    KnobSocketMessageType.TEST_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
//            "5" -> {
//                sendMessage(
//                    KnobSocketMessage.SET_WIFI,
//                    ssid = ssid,
//                    password = password,
//                    securityType = securityType
//                )
//            }
        }

    }

    fun sendMessageInVM(
        type: KnobSocketMessageType,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = launch(ioContext) {
        socketManager.sendMessage(type, ssid, password, securityType)
    }

    private suspend fun sendMessage(
        type: KnobSocketMessageType,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = socketManager.sendMessage(type, ssid, password, securityType)

}
