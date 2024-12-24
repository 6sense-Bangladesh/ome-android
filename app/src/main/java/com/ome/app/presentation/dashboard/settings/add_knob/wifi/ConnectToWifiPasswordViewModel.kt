package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.*
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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

    private var setWifiSuccess = false
    private var wifiRebootTried = false
    var socketReconnect = 0
    private var credentialFail = 0
    private var retryWifi = 10

    fun initListeners() {
        socketManager.messageReceived = { type, message ->
            launch(ioContext) {
                when (type) {
                    KnobSocketMessageType.TEST_WIFI -> {
                        if (message == "ok" || message.toIntOrNull() != null) {
                            sendMessage(KnobSocketMessageType.WIFI_STATUS)
                        } else if (message == "reset") {
                            delay(2.seconds)
                            connectToSocket()
                        }
                    }
                    KnobSocketMessageType.WIFI_STATUS -> {
                        handleWifiStatusMessage(message)
                    }
                    KnobSocketMessageType.SET_WIFI -> {
                        if (message == "ok") {
                            setWifiSuccess = true
                            clearListeners()
                            delay(3.seconds)
                            sendMessage(KnobSocketMessageType.REBOOT)
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
        var connectJob: Job? = null
        networkManager.onConnectionChange = {
            loadingLiveData.value.log("NetworkManager onConnectionChange loadingLiveData")
            if (!it && !setWifiSuccess && loadingLiveData.value != false) {
                connectJob?.cancel()
                connectJob = launch(ioContext + SupervisorJob()) {
                    delay(1.seconds * socketReconnect)
                    connectToWifi()
                }
            }
        }
        socketManager.onSocketConnect = {
            launch(ioContext) {
                if(it && socketReconnect > 0){
                    delay(1.seconds * socketReconnect)
                    if(socketManager.isConnected)
                        testWifi()
                    else {
                        socketReconnect++
                        connectToSocket()
                    }
                }
                else{
                    delay(1.seconds * socketReconnect)
                    if(loadingLiveData.value == false) return@launch
                    if(!networkManager.isConnected)
                        connectToWifi()
                    else if(socketReconnect > 0)
                        connectToSocket()
                }
            }

        }
    }

    fun clearListeners() {
        socketManager.messageReceived = {_,_ -> }
        networkManager.onConnectionChange = {}
        socketManager.onSocketConnect = {}
    }

    private suspend fun disconnectFromNetwork(){
        delay(3.seconds)
        MAIN { networkManager.disconnectFromKnobHotspot() }
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
                wifiRebootTried = false
                sendMessage(KnobSocketMessageType.REBOOT)
            }
            "1" -> {
                testWifi()
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
            else -> { // "2" , others
                credentialFail++
                if (credentialFail < 10 && !wifiRebootTried || credentialFail < 5) {
                    delay(3.seconds)
                    sendMessage(KnobSocketMessageType.WIFI_STATUS)
                }else{
                    credentialFail = 0
                    if(!wifiRebootTried) {
                        setWifiSuccess = false
                        wifiRebootTried = true
                        sendMessage(KnobSocketMessageType.REBOOT)
                        delay(4.seconds)
                        connectToWifi()
//                        testWifi()
                    }else {
                        defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
                        wifiRebootTried = false
                        sendMessage(KnobSocketMessageType.REBOOT)
                    }
                }
            }
        }

    }

    private var connectSocketJob : Job? = null
    private fun connectToSocket(){
        connectSocketJob?.cancel()
        connectSocketJob = launch(ioContext + SupervisorJob()){
            socketReconnect++
            socketManager.connect()
        }
    }
    private suspend fun connectToWifi(){
        "socketReconnect $socketReconnect".log("connectToWifi")
        socketReconnect++
        connectionStatusListener.shouldReactOnChanges = false
        if(networkManager.isConnected) {
            if(!socketManager.isConnected)
                connectToSocket()
            return
        }
        val result = tryGet { networkManager.connectToKnobHotspot() }


        //Check whether device connected to wifi or not
        while (result?.first.isFalse() && retryWifi > 0 && !setWifiSuccess) {
            delay(1.seconds)
            connectToWifi()
            retryWifi--
        }
        if(retryWifi == 0) {
            retryWifi = 10
            wifiRebootTried = false
            sendMessage(KnobSocketMessageType.REBOOT)
            error(resourceProvider.getString(R.string.incorrect_network_name_and_password))
        }
        else connectToSocket()
    }

    fun testWifi(pass: String = password) = launch(ioContext) {
        password = pass
        if(pass.isBlank()) {
            defaultErrorLiveData.postValue(resourceProvider.getString(R.string.incorrect_network_name_and_password))
            return@launch
        }
        if(networkManager.isConnected && socketManager.isConnected) {
            credentialFail = 0
            sendMessage(
                type = KnobSocketMessageType.TEST_WIFI,
                ssid = ssid,
                password = pass,
                securityType = securityType
            )
        }
        else if(!networkManager.isConnected)
            connectToWifi()
        else
            connectToSocket()
    }

    private suspend fun sendMessage(
        type: KnobSocketMessageType,
        ssid: String = "",
        password: String = "",
        securityType: String = ""
    ) = socketManager.sendMessage(type, ssid, password, securityType)

}
