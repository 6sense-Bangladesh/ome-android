package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.NetworkManager
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.local.SocketManager
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.MAIN
import com.ome.app.utils.isFalse
import com.ome.app.utils.isTrue
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ConnectToWifiPasswordViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val socketManager: SocketManager,
    private val resourceProvider: ResourceProvider,
    private val connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    var macAddr = ""
    var ssid = ""
    var securityType = ""
    var password = ""

    fun initListeners() {
        socketManager.messageReceived = { type, message ->
            launch(ioContext){
                when (type) {
//                    KnobSocketMessageType.REBOOT -> {
//                        rebootRetries++
//                        if (rebootRetries < 3)
//                            sendMessage(KnobSocketMessageType.REBOOT)
//                        else
//                            defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
////                    successMessageLiveData.postValue(resourceProvider.getString(R.string.connection_success))
////                    loadingLiveData.postValue(false)
//                    }
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
                            delay(3.seconds)
                            sendMessage(KnobSocketMessageType.REBOOT)
                            socketManager.stopClient()
                            disconnectFromNetwork()
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
        var connectJob : Job? = null
        networkManager.onConnectionChange = {
            if(connectJob == null && !it) {
                connectJob = launch(ioContext) {
                    connectToWifi()
                    socketManager.connect()
                    connectJob = null
                }
            }
        }
        socketManager.onSocketConnect = {
            it.isTrue{
                launch {
                    testWifi(password)
                }
            }.isFalse{
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
            }
        }
    }

    private suspend fun disconnectFromNetwork(){
        delay(3.seconds)
        MAIN { networkManager.disconnectFromKnobHotspot() }
        connectionStatusListener.shouldReactOnChanges = true
    }

    private var credentialFail = 0
    private var wifiReboot = 0


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
                credentialFail++
                if(credentialFail < 12) {
                    delay(4.seconds)
                    sendMessage(KnobSocketMessageType.WIFI_STATUS)
                }else{
                    credentialFail = 0
                    if(wifiReboot==0) {
                        wifiReboot++
                        sendMessage(KnobSocketMessageType.REBOOT)
                        socketManager.stopClient()
                        delay(5.seconds)
                        connectToWifi()
                        socketManager.connect()
                        sendMessage(
                            KnobSocketMessageType.TEST_WIFI,
                            ssid = ssid,
                            password = password,
                            securityType = securityType
                        )
                    }else {
                        defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
                        wifiReboot = 0
                    }
                }
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

    private suspend fun connectToWifi(){
        "connectToWifi".log("WifiHandler")
        connectionStatusListener.shouldReactOnChanges = false
        val result = networkManager.connectToKnobHotspot()

        //Check whether device connected to wifi or not
        if (result.first) {
            socketManager.connect()
        } else {
            result.second?.let { message ->
                error(message)
            } ?: connectToWifi()
        }
    }

    fun testWifi(pass: String) = launch(ioContext) {
        if(networkManager.isConnected) {
            credentialFail = 0
            password = pass
            sendMessage(
                type = KnobSocketMessageType.TEST_WIFI,
                ssid = ssid,
                password = pass,
                securityType = securityType
            )
        }
        else connectToWifi()
    }

    private suspend fun sendMessage(
        type: KnobSocketMessageType,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = socketManager.sendMessage(type, ssid, password, securityType)

}
