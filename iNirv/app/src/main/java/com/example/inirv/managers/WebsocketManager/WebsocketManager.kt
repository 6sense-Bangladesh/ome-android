   package com.example.inirv.managers.WebsocketManager

import android.net.Uri
import com.example.inirv.managers.KnobManager
import com.example.inirv.managers.UserManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.util.concurrent.TimeUnit

interface WebsocketManagerDelegate{
    fun handleWebsocketResponse(response: WebsocketManager.WebsocketResponse)
}

object WebsocketManager: OmeWebsocketListenerDelegate {

    var delegate: WebsocketManagerDelegate? = null
        private set
    var websocketListener: OmeWebsocketListener? = null
        private set
    var webSocket: WebSocket? = null
    var userManager: UserManager = UserManager
    var knobManager: KnobManager = KnobManager
    private var isConnected: Boolean = false



    var websocketGateway: String = ""   // Gateway the restmanager will connect to for socket updates
    private val wsQueryString = "inirvUid"      // Header for the uid that all gatways need to open

    init {
        // TODO: Need to make this dynamic for the different forms of apps we could have
        this.setup(1)
    }

    /* Apptype
    1 - Development
    2 - Sandbox
    3 - Production
    4 - DEMO
    * */
    private fun setup(appType: Int){

        when (appType) {
            1 -> {
                // Development(Internal) Gateways
                websocketGateway = "app-ws-dev.api.omekitchen.com"
            }
            2 -> {
                // Production Gateways
                websocketGateway = "wss://app-ws.api.omekitchen.com"
            }
            3 -> {
                // Sandbox Gateways
                websocketGateway = "wss://app-ws-sandbox.api.omekitchen.com"
            }

            4 -> {
                // DEMO gateways
                websocketGateway = "wss://app-ws-dev.api.omekitchen.com"
            }
        }

        createWebsocketListener()
    }

    private fun createWebsocketListener(){

        this.websocketListener = OmeWebsocketListener(this)
    }

    fun setDelegate(delegate: WebsocketManagerDelegate){
        this.delegate = delegate
    }

    fun connectToWebsocket(macIDList: List<String>) = runBlocking{

        if (isConnected){
            return@runBlocking
        }

        // Put the macIDs in a comma separated list
        var macIDs = ""
        for (macID in macIDList){
            macIDs += "$macID,"
        }

        macIDs = macIDs.removeSuffix(",")

        // "\(websocketGateway)?knobMacAddr=\(macIDs)&\(wsQueryString)=\(userProfileManager!.userId)"
        val client = OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS).build()

        val builder = Uri.Builder()
        builder.scheme("wss")
            .authority(websocketGateway)
            .appendQueryParameter("knobMacAddr", macIDs)
            .appendQueryParameter(wsQueryString, UserManager.user?.userId)
            .build()

        print("WM: builder: ${builder}")
        print("")

         launch {
             val request = Request.Builder().url(builder.toString()).build()
             println("WM: request: $request")
             isConnected = true
             webSocket = client.newWebSocket(request, websocketListener!!)
        }

//        Thread.sleep(2000)

//        exitProcess(0)
    }

    fun disconnectFromWebsocket(){

        // Tells the websocket to close off it's connection
        webSocket?.close(1000, "User is logging out")
    }

    override fun receivedWebsocketMsg(responseText: String, responseBytes: ByteString?) {
//        TODO("Not yet implemented")
//        println("WM: receivedWebsocketMsg: responseText $responseText")
//        parseMessage(responseText)
        val response: WebsocketResponse = Json.decodeFromString(responseText)
        delegate?.handleWebsocketResponse(response)
    }

    override fun websocketClosing(code: Int, reason: String) {
//        TODO("Not yet implemented")
        isConnected = false
    }

    override fun websocketFailed(throwable: Throwable, response: Response?) {
//        TODO("Not yet implemented")
        println("WM: websocketFailed, throwable: $throwable,\nWM: websocketFailed, response: $response")
    }

    @Serializable
    data class WebsocketResponse(
        val macAddr: String,
        val name: String,
        val value: @Contextual Any
    )

    // Parse the given message from JSON to a dictionary
    fun parseMessage(message: String){

        println("WM: parseMessage: message: $message")

        val response = Json.decodeFromString<Map<String, @Polymorphic Any>>(message) //decodeFromString<MutableMap<String,@Polymorphic Any>>(message)

        println("WM: parseMessage: response: $response")
    }
}