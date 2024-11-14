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

    private var rebootRetries = 0


    fun initListeners() {
        socketManager.messageReceived = { type, message ->
            launch(ioContext){
                when (type) {
                    KnobSocketMessageType.REBOOT -> {
                        rebootRetries++
                        if (rebootRetries < 3)
                            sendMessage(KnobSocketMessageType.REBOOT)
                        else
                            defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
//                    successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
//                    loadingLiveData.postValue(false)
                    }
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
//                        disconnectFromNetwork()
                            delay(2.seconds)
                            successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
                            loadingLiveData.postValue(false)
                        } else {
                            defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
                        }
                    }
                    else -> Unit
                }
            }
        }
//
//        socketManager.onSocketConnect = {
//        }
    }

    private suspend fun disconnectFromNetwork(){
        delay(3.seconds)
        MAIN { wifiHandler.disconnectFromNetwork() }
        connectionStatusListener.shouldReactOnChanges = true
    }


    private suspend fun handleWifiStatusMessage(message: String) {
        when (message) {
            "0", "3" -> {
                // The credentials worked so now we can set the password and network name for the knob
                sendMessage(
                    KnobSocketMessageType.SET_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
            "4", "6" -> {
                // Something went wrong and we'll need to try again with new credentials or something
                // We need to reset the process
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
            }
            "2" -> {
                delay(4.seconds)
                sendMessage(KnobSocketMessageType.WIFI_STATUS)
            }
            "1" -> {
                sendMessage(
                    KnobSocketMessageType.TEST_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
            "5" -> {
                // Something went wrong and we'll need to try again with new credentials or something
                // We need to reset the process

                // MARK: For now just treat this like a successful 0 or 3.  But still send up the message so we can know when a 5 was received
                sendMessage(
                    KnobSocketMessageType.SET_WIFI,
                    ssid = ssid,
                    password = password,
                    securityType = securityType
                )
            }
        }

    }

    fun testWifi(pass: String) = launch(ioContext) {
        password = pass
        sendMessage(
            type = KnobSocketMessageType.TEST_WIFI,
            ssid = ssid,
            password = pass,
            securityType = securityType
        )
    }

    private suspend fun sendMessage(
        type: KnobSocketMessageType,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = socketManager.sendMessage(type, ssid, password, securityType)

}
