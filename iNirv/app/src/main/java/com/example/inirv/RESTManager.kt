package com.example.inirv

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
    IOSINFO
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
    fun handleWebSocketResponse(response: MutableMap<String, Any>)
    fun errorReceived(error: String)
}

class RESTManager {

    // MARK: Constants
    private val restAPIHeader = "x-inirv-uid"   // Header for the uid that all rest commands need
    private val wsQueryString = "inirvUid"      // Header for the uid that all gatways need to open
    private val versionHeader = "x-inirv-vsn"   // Header for the version of the endpoints we want to use
    private val currVersionNumber = "6"         // Current version for the version header
    private val authHeader: String = "x-inirv-auth"    // Header needed for the access token

    // MARK: Variables
    var restAPIGateway: String = "" // Gatway the restmanager will connect to for REST commands
    var websocketGateway: String = ""   // Gateway the restmanager will connect to for socket updates
    val userID: String = "" // The id that the rest/websockets will be using for communication.  This gets set by the userprofile manager

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
                restAPIGateway = "https://app-dev.api.omekitchen.com"
                websocketGateway = "wss://app-ws-dev.api.omekitchen.com"
            }
            2 -> {
                // Production Gateways
                restAPIGateway = "https://app.api.omekitchen.com"
                websocketGateway = "wss://app-ws.api.omekitchen.com"
            }
            3 -> {
                // Sandbox Gateways
                restAPIGateway = "https://app-sandbox.api.omekitchen.com"
                websocketGateway = "wss://app-ws-sandbox.api.omekitchen.com"
            }

            4 -> {
                // DEMO gateways
                restAPIGateway = "https://app-dev.api.omekitchen.com"
                websocketGateway = "wss://app-ws-dev.api.omekitchen.com"
            }
        }
    }

    fun setupRESTCommand(msgType: RESTCmdType, params: MutableMap<String, Any>, delegate: RESTManagerDelegate){


    }
}