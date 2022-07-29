   package com.example.inirv.managers.WebsocketManager

import android.net.Uri
import com.example.inirv.managers.KnobManager
import com.example.inirv.managers.UserManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.util.concurrent.TimeUnit

interface WebsocketManagerDelegate{
    fun handleWebsocketResponse(response: MutableMap<String, Any>)
}

object WebsocketManager: OmeWebsocketListenerDelegate {

    var delegate: WebsocketManagerDelegate? = null
        private set
    var websocketListener: OmeWebsocketListener? = null
        private set
    var webSocket: WebSocket? = null
    var userManager: UserManager = UserManager
    var knobManager: KnobManager = KnobManager



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

    // TODO: Finish connectToWebsocket Implementation
    fun connectToWebsocket(macIDList: List<String>) = runBlocking{

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
            .appendQueryParameter(wsQueryString, UserManager.userID)
            .build()

         launch {
            val request = Request.Builder().url(builder.toString()).build()
            println("WM: request: $request")

            webSocket = client.newWebSocket(request, websocketListener!!)
        }

//        Thread.sleep(2000)

//        exitProcess(0)
    }

    fun disconnectFromWebsocket(){

        // Tells the websocket to close off it's connection
        webSocket?.close(0, "User is logging out")
    }

    override fun receivedWebsocketMsg(responseText: String, responseBytes: ByteString?) {
//        TODO("Not yet implemented")
        println("WM: receivedWebsocketMsg: responseText $responseText")
        parseMessage(responseText)
    }

    override fun websocketClosing(code: Int, reason: String) {
//        TODO("Not yet implemented")
    }

    override fun websocketFailed(throwable: Throwable, response: Response?) {
//        TODO("Not yet implemented")
        println("WM: websocketFailed, throwable: $throwable,\nWM: websocketFailed, response: $response")
    }

    // Parse the given message from JSON to a dictionary
    fun parseMessage(message: String){

        println("WM: parseMessage: message: $message")

        val response = Json.decodeFromString<MutableMap<String,@Polymorphic Any>>(message)

        println("WM: parseMessage: response: $response")
    }
}