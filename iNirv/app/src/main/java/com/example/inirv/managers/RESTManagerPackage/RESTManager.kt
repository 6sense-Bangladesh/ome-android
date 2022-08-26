package com.example.inirv.managers

import android.net.Uri
import android.util.Log
import com.example.inirv.managers.WebsocketManager.WebsocketManager.websocketGateway
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException

enum class RESTCmdType{
    TEST,
    USER,
    USERIMG,
    KNOB,
    ALLKNOBS,
    KNOBANGLE,
    SCHED,
    STARTSCHED,
    STOPSCHED,
    SAFETYLOCKON,
    SAFETYLOCKOFF,
    CLEARWIFI,
    SETCALIBRATION,
    OWNERSHIP,
    PAUSESCHED,
    NONE,
    INITCALIB,
    ADDUSERIMAGE,
    RESETAUTOSHUTOFFTIMER,
    ANDROIDINFO
}

enum class RESTMethodType{
    GET,
    POST,
    PATCH,
    DELETE,
    NONE,
    PUT
}

interface RESTManagerDelegate{

    fun handleResponse(response: String, commandType: RESTCmdType, methodType: RESTMethodType)
    fun errorReceived(error: String)
}

object RESTManager: WebSocketListener() {

    // MARK: Constants
    private val restAPIHeader = "x-inirv-uid"   // Header for the uid that all rest commands need
    private val versionHeader = "x-inirv-vsn"   // Header for the version of the endpoints we want to use
    private val currVersionNumber = "6"         // Current version for the version header
    private val authHeader: String = "x-inirv-auth"    // Header needed for the access token


    // MARK: Variables
    var restAPIGateway: String = "" // Gatway the restmanager will connect to for REST commands
    private var userID: String = "" // The id that the rest/websockets will be using for communication.  This gets set by the userprofile manager
    private var knobManagerDelegate: RESTManagerDelegate? = null
    private var userManagerDelegate: RESTManagerDelegate? = null
    private var wasDisconnected: Boolean = false
    private var amplifyManager: AmplifyManager = AmplifyManager

    init {
        this.setup(1)
    }

    fun setUserID(newUserID: String){

        this.userID = newUserID
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
                restAPIGateway = "app-dev.api.omekitchen.com"
                websocketGateway = "app-ws-dev.api.omekitchen.com"
            }
            2 -> {
                // Production Gateways
                restAPIGateway = "app.api.omekitchen.com"
                websocketGateway = "wss://app-ws.api.omekitchen.com"
            }
            3 -> {
                // Sandbox Gateways
                restAPIGateway = "/app-sandbox.api.omekitchen.com"
                websocketGateway = "wss://app-ws-sandbox.api.omekitchen.com"
            }

            4 -> {
                // DEMO gateways
                restAPIGateway = "app-dev.api.omekitchen.com"
                websocketGateway = "wss://app-ws-dev.api.omekitchen.com"
            }
        }
    }

    fun setAmplifyManager(amplifyManager: AmplifyManager){
        this.amplifyManager = amplifyManager
    }

    fun setupRESTCommand(msgType: RESTCmdType, params: MutableMap<String, Any> = mutableMapOf(),
                         methodType: RESTMethodType, delegate: RESTManagerDelegate){

        // Check that we have a user id for the api call OR that it's a call to get the android version info
        if (userID.isEmpty() && msgType != RESTCmdType.ANDROIDINFO){
            // Otherwise send up an error
            delegate.errorReceived("No user ID for the REST API Call")
            return
        }

        // TODO: Eventually need to grab the token but it's not necessary now
//        // Get the auth access token if we need it for a call
//        if (msgType != RESTCmdType.ANDROIDINFO){
//            getAuthAccessToken(msgType, params, methodType, delegate)
//        } else {
            // Otherwise just send the command without the token
            sendRESTCommand(msgType,params, methodType, delegate)
//        }

    }

    fun generatePath(cmdType: RESTCmdType, params: MutableMap<String, Any>): String {

        // Return value
        var path = ""

        // Generate the appropriate path based on the command type presented
        when(cmdType){
            RESTCmdType.TEST -> {
                path = "hello"
            }
            RESTCmdType.USER -> {
                path = "user"
            }
            RESTCmdType.USERIMG -> {
                path = "user/uploadImageUrl?fileName=${params.getValue("fileName")}"
            }
            RESTCmdType.KNOB -> {
                path = "knob/${params.getValue("macID")}"
            }
            RESTCmdType.ALLKNOBS -> {
                path = "knobs"
            }
            RESTCmdType.KNOBANGLE -> {
                path = "knob/newLevel/${params.getValue("macID")}"
            }
            RESTCmdType.SCHED -> {
                path = "knob/schedule/${params.getValue("macID")}"
            }
            RESTCmdType.STARTSCHED -> {
                path = "knob/schedule/${params.getValue("macID")}/start"
            }
            RESTCmdType.STOPSCHED -> {
                path = "knob/schedule/${params.getValue("macID")}/stop"
            }
            RESTCmdType.SAFETYLOCKON -> {
                path = "knob/safetyLock/${params.getValue("macID")}/on"
            }
            RESTCmdType.SAFETYLOCKOFF -> {
                path = "knob/safetyLock/${params.getValue("macID")}/off"
            }
            RESTCmdType.CLEARWIFI -> {
                path = "knob/clearWifi/${params.getValue("macID")}"
            }
            RESTCmdType.SETCALIBRATION -> {
                path = "knob/calibration/${params.getValue("macID")}"
            }
            RESTCmdType.OWNERSHIP -> {
                path = "knob/ownership/${params.getValue("macID")}"
            }
            RESTCmdType.PAUSESCHED -> {
                path = "knob/schedule/${params.getValue("macID")}/pause"
            }
            RESTCmdType.INITCALIB -> {
                path = "knob/initCalibration/${params.getValue("macID")}"
            }
            RESTCmdType.RESETAUTOSHUTOFFTIMER -> {
                path = "knob/refreshAutoOff/${params.getValue("macID")}"
            }
            RESTCmdType.ANDROIDINFO -> {
                path = "iosAppInfo"
            }
        }

        // Return the path that was calculated from the msg type
        return path
    }

    // TODO: Finish sendRESTCommand Implementation
    private fun sendRESTCommand(command: RESTCmdType, params: MutableMap<String, Any>,
                        method: RESTMethodType, delegate: RESTManagerDelegate, accessToken: String = ""){

        // Generate the gateway and path
        val urlString = restAPIGateway
        val pathString = generatePath(command, params)

        var bodyParams: MutableMap<String, Any> = mutableMapOf()
        val pass = Unit

        // Add the appropriate parameters if it's a given command/method pair
        when {
            ((command == RESTCmdType.USER) && (method == RESTMethodType.POST)) ||
                    ((command == RESTCmdType.USER) && (method == RESTMethodType.PATCH)) ||
                    ((command == RESTCmdType.KNOB) && (method == RESTMethodType.POST)) ||
                    ((command == RESTCmdType.KNOB) && (method == RESTMethodType.PATCH)) ||
                    ((command == RESTCmdType.KNOBANGLE) && (method == RESTMethodType.POST)) ||
                    ((command == RESTCmdType.STARTSCHED) && (method == RESTMethodType.POST)) ||
                    ((command == RESTCmdType.STOPSCHED) && (method == RESTMethodType.POST)) ||
                    ((command == RESTCmdType.INITCALIB) && (method == RESTMethodType.POST)) -> {

                // Go through each of the parameters and add them to request
                params.forEach { entry ->

                    // Continue if the entry is the macID
                    if (entry.key == "macID"){
                        return@forEach
                    }

                    // Create a string array from the comma separated string,
                    // if the entry is the device tokens
                    if (entry.key == "deviceTokens"){

                        var deviceTokenArray: MutableList<String> = (entry.value as String).split(",").toMutableList()

                        if((entry.value as String).isEmpty()){
                            deviceTokenArray.clear()
                        }
                    }

                    // Update the body parameters map to include the key/value pair
                    bodyParams[entry.key] = entry.value
                }
            }
        }

        val builder = Uri.Builder()
        builder.scheme("https")
            .authority(urlString)
            .appendPath(pathString)

        // Add the parameters to the url (if there are any)
        bodyParams.forEach{ entry ->
            // TODO: Need to look into how to do this for multiple devices.  For now we're
            //  just assuming everything is a string
            builder.appendQueryParameter(entry.key, entry.value as String)
        }

        Log.v("RestManager", "Builder: $builder")

        val request = Request.Builder()
            .url(builder.build().toString())

        // Add appropriate headers
        if (command != RESTCmdType.ANDROIDINFO){
            request.addHeader(restAPIHeader, userID)
            request.addHeader(authHeader, accessToken)
        }

        request.addHeader("x-inirv-vsn", currVersionNumber)

        // TODO: Need to add this value if we are in the DEMO version of the application
//        request.addHeader("x-inirv-demo", "1")
        // TODO: EoT

        Log.v("restManager", "Request: $request")
        OkHttpClient().newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Send the error to the delegate
                delegate.errorReceived(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                 Log.i("restManager", "Call succesful")

                //Check if the value is null
                val responseString = response.body?.string()?.let{
                    // Send the response to the delegate
                    delegate.handleResponse(it, command, method)
                } ?: {
                    // Send the error to the delegate
                    delegate.errorReceived("The response couldn't be grabbed")
                }

            }
        })
    }

    fun generateMethod(methodType: RESTMethodType): String{
        var method: String = "GET"

        when(methodType){
            RESTMethodType.GET -> {
                method = "GET"
            }
            RESTMethodType.POST -> {
                method = "POST"
            }
            RESTMethodType.PATCH -> {
                method = "PATCH"
            }
            RESTMethodType.DELETE -> {
                method = "DELETE"
            }
            else -> {
                method = "GET"
            }
        }
        return method
    }

    // TODO: Finish addUserImageToBackend Implementation
    // TODO: Need to determine data type for the imageData
    fun addUserImageToBackend(urlPath: String/*, imageData: Data*/){

    }

    // TODO: Finish connectToWebsocket Implementation
    fun connectToWebsocket(macIDList: Array<String>){

    }

    // TODO: Finish disconnectFromWebsocket Implementation
    fun disconnectFromWebsocket(){

    }

    // TODO: Finish getAuthAccessToken Implementation
    fun getAuthAccessToken(cmdType: RESTCmdType, params: MutableMap<String, Any>, methodType: RESTMethodType, delegate: RESTManagerDelegate) = runBlocking{

        launch {
            // Tell the amplify manager to get the current authentication session
            amplifyManager.fetchAuthSession { amplifyResultValue ->

                // Parse then result
                parseFetchAuthSessionResponse(amplifyResultValue, cmdType, params, methodType, delegate)
            }
        }

    }

    // TODO: Finish parseFetchAuthSessionResponse Implementation
    fun parseFetchAuthSessionResponse(resultValue: AmplifyResultValue, cmdType: RESTCmdType, params: MutableMap<String, Any>, methodType: RESTMethodType, delegate: RESTManagerDelegate){

    }
}


