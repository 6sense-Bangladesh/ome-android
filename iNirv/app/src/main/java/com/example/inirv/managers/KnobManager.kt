package com.example.inirv.managers

import android.util.Log
import com.example.inirv.Knob.Knob
import com.example.inirv.Knob.KnobZone
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import com.example.inirv.managers.WebsocketManager.WebsocketManagerDelegate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface KnobManagerDelegate{

    fun kmHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    )

    fun kmWebsocketResponse(
        //response: MutableMap<String, Any>
        response: WebsocketManager.WebsocketResponse
    )
}

@Serializable
data class CalibrationResponse(
    val rotationDir: Int,
    val offAngle: Int,
    val zones: List<ZoneResponse>
)

@Serializable
data class ZoneResponse(
    val zoneNumber: Int,
    val zoneName: String,
    val lowAngle: Int,
    val mediumAngle: Int,
    val highAngle: Int,
)

@Serializable
data class KnobResponse(
    val mountingSurface: String,
    val ipAddress: String,
    val safetyLock: Boolean,
    val scheduleFinishTime: Int,
    val connectStatus: String,
    val angle: Int,
    val temperature: Double,
    val battery: Int,
    val firmwareVersion: String,
    val updated: String,
    val schedulePauseRemainingTime: Int,
    val lastScheduleCommand: String,
    val batteryVolts: Double,
    val macAddr: String,
    val scheduleStartTime: Int,
    val userId: String,
    val stoveId: String,
    val stovePosition: Int,
    val gasOrElectric: String,
    val calibrated: Boolean,
    val calibration: CalibrationResponse
)

object KnobManager: WebsocketManagerDelegate, RESTManagerDelegate {

    var knobManagerDelegate: KnobManagerDelegate? = null
        private set
    var restManager: RESTManager = RESTManager
        private set
    var knobs: List<Knob> = listOf()
        private set
    var knobOrder: List<String> = listOf()
        private set
    var webSocketManager: WebsocketManager = WebsocketManager
        private set

    fun connectWebsocket(){

        val macIDArray: List<String> = getKnobList()
        webSocketManager.setDelegate(this)
        webSocketManager.connectToWebsocket(macIDArray)
    }

    fun setup(){

        // Tell the restmanager to get the knobs associated with this user
        restManager.setupRESTCommand(
            msgType = RESTCmdType.ALLKNOBS,
            methodType = RESTMethodType.GET,
            delegate = this
        )
    }

    fun getKnob(macID: String): Knob?{

        return null
    }

    fun getKnobAt(position: Int): Knob?{

        for (knob in knobs){

            if (knob.mStovePosition == position){
                return knob
            }
        }

        return null
    }

    // TODO: Needs to be implemented appropriately
    private fun createKnob(knobsResponse: KnobResponse): Knob{

        val knob = Knob(
            macID = knobsResponse.macAddr,
            firmwareVersion = knobsResponse.firmwareVersion,
            ipAddress = knobsResponse.ipAddress,
            safetyLockEnabled = knobsResponse.safetyLock,
            stoveID = knobsResponse.stoveId,
            stovePosition = knobsResponse.stovePosition,
            userID = knobsResponse.userId,
            schedulePauseRemainingTime = knobsResponse.schedulePauseRemainingTime,
            lastScheduledCommand = knobsResponse.lastScheduleCommand,
            connection = knobsResponse.connectStatus,
            currLevel = knobsResponse.angle
        )

        val calibrationResponse = knobsResponse.calibration

        knob.mOffAngle = calibrationResponse.offAngle
        knob.mIsOff = calibrationResponse.offAngle == knobsResponse.angle

        var zones = mutableListOf<KnobZone>()
        for (zoneResponse in calibrationResponse.zones){
            val zone = KnobZone(
                zoneResponse.zoneNumber,
                zoneResponse.zoneName,
                calibrationResponse.rotationDir,
                zoneResponse.lowAngle,
                zoneResponse.mediumAngle,
                zoneResponse.highAngle
            )

            zones.add(zone)
        }

        knob.mZones = zones

        // TODO: Add in timer stuff

        return knob
    }

    private fun getKnobList(): List<String> {

        var listOfMacIDs: MutableList<String> = mutableListOf()

        for (knob in knobs){
            listOfMacIDs.add(knob.mMacID)
        }

        return listOfMacIDs.toList()
    }

    fun removeAllKnobs(){
        knobs = listOf()
    }

    fun setDelegate(delegate: KnobManagerDelegate){
        this.knobManagerDelegate = delegate
    }

    fun setRESTManager(restManager: RESTManager){
        this.restManager = restManager
    }

    fun removeDelegate(delegate: KnobManagerDelegate){

        if(delegate == this.knobManagerDelegate){
            knobManagerDelegate = null
        }
    }

    fun setWebsocketManager(websocketManager: WebsocketManager){
        this.webSocketManager = websocketManager
    }

    fun sendRestCommand(params: MutableMap<String, Any> = mutableMapOf(), commandType: RESTCmdType, methodType: RESTMethodType){

        restManager.setupRESTCommand(commandType, params, methodType, this)
    }

    // MARK: WebsocketManagerDelegate
    override fun handleWebsocketResponse(response: WebsocketManager.WebsocketResponse) {
        this.knobManagerDelegate?.kmWebsocketResponse(response)
    }
//    override fun handleWebsocketResponse(response: MutableMap<String, Any>) {
//
//        this.knobManagerDelegate?.kmWebsocketResponse(response)
//    }

    // MARK: RESTManagerDelegate
    override fun handleResponse(
        response: String,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        val parsedResponse = mutableMapOf<String, String>()//parseResponse(response, commandType)

        if (commandType == RESTCmdType.ALLKNOBS && methodType == RESTMethodType.GET){

            setupKnobs(Json.decodeFromString(response))
        } else if((commandType == RESTCmdType.SAFETYLOCKOFF || commandType == RESTCmdType.SAFETYLOCKON) && methodType == RESTMethodType.POST){

            for (knob in knobs){
                knob.mSafetyLockEnabled = (commandType == RESTCmdType.SAFETYLOCKON)
            }
        } else if(commandType == RESTCmdType.KNOB && methodType == RESTMethodType.PATCH){

            val retrievedKnob = createKnob(Json.decodeFromString(response))

            val getKnob = getKnob(retrievedKnob.mMacID)

            var tempKnobs = knobs.toMutableList()

            if (getKnob == null){
                tempKnobs.add(retrievedKnob)
            } else {
                tempKnobs.set(tempKnobs.indexOf(getKnob), retrievedKnob)
            }

            knobs = tempKnobs.toList()
        } else if(commandType == RESTCmdType.INITCALIB && methodType == RESTMethodType.POST){

            updateKnob(parsedResponse, "knobSetCalibration")
        }

        knobManagerDelegate?.kmHandleResponse(parsedResponse as MutableMap<String, Any>, commandType, methodType)
    }

    override fun errorReceived(error: String) {
        knobManagerDelegate?.kmHandleResponse(
            mutableMapOf(Pair("error", error)),
            RESTCmdType.NONE,
            RESTMethodType.NONE
        )
    }

    /**
     * First value of the returned Pair is for the default responses.
     * The second value is for the all knobs response
     */

    private fun parseResponse(message: String, commandType: RESTCmdType):
            Pair<
                Map<String, String>,
                List<Map<String, String>>
            >
    {
        Log.i("kmanager parseMessage", "message: $message")
        val response: Pair<Map<String, String>, List<Map<String, String>>> =
            when(commandType){
                RESTCmdType.ALLKNOBS -> {
                    Pair(mapOf(), Json.decodeFromString(message))
                }
                else -> {
                    Pair(Json.decodeFromString(message), listOf())
                }
            }

        return response
    }


    private fun setupKnobs(parsedKnobResponse: List<KnobResponse>){

        var tempKnobs: MutableList<Knob> = mutableListOf()

        for (knobResponse in parsedKnobResponse){

            tempKnobs.add(createKnob(knobResponse))
        }

        knobs = tempKnobs

        connectWebsocket()
    }

    // TODO: Need to implement
    private fun updateKnob(
        response: Map<String, String>,
        commandName: String
    ){


    }
}