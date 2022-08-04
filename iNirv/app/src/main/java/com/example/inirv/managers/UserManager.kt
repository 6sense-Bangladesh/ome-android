package com.example.inirv.managers

import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import com.example.inirv.managers.WebsocketManager.WebsocketManagerDelegate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class UserResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val email: String,
    val phone: String,
    val uiAppType: String,
    val uiAppVersion: String,
    val deviceTokens: List<String>,
    val stoveId: String,
    val stoveMakeModel: String,
    val stoveOrientation: Int,
    val stoveGasOrElectric: String,
    val stoveAutoOffMins: Int,
    val stoveSetupComplete: Boolean,
    val numKnobs: Int,
    val knobMacAddrs: List<String>,
)

interface UserManagerDelegate{

    fun umHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    )

    fun umWebsocketResponse(
        response: MutableMap<String, Any>
    )
}

object UserManager: RESTManagerDelegate, WebsocketManagerDelegate{

    var restManager = RESTManager
        private set
    var userID = ""
    var numKnobs: Int = 0
    var stoveSetupComplete: Boolean = false
        private set
    var userManagerDelegate: UserManagerDelegate? = null
        private set
    var amplifyManager: AmplifyManager = AmplifyManager
        private set
    var deviceTokens: MutableList<String> = mutableListOf()
        private set
    var stoveOrientation: Int = -1
        private set
    var email: String = ""
        private set
    var phoneNumber: String = ""
        private set
    var firstName: String = ""
        private set
    var middleName: String = ""
        private set
    var lastName: String = ""
        private set
    var knobListSize: Int = 0
        private set
    var stoveAutoOffMins: Int = 15
        private set
    var stoveMakeModel: String = ""
        private set
    var stoveType: String = ""
        private set
    var uiAppVersion: String = "1.0"
        private set
    var user: UserResponse? = null
        private set


    // TODO: Remove when done testing
    fun sendRestCmd(){

        /*  
        val cmdType = RESTCmdType.ALLKNOBS
        val methodType = RESTMethodType.GET
        val params = mutableMapOf<String, Any>()

        restManager.setupRESTCommand(cmdType, params, methodType, this)
         */
        
        val macIDs: List<String> = listOf("588E815CAC24")
//        restManager.connectToWebsocket(macIDs)
        WebsocketManager.connectToWebsocket(macIDs)
    }
    // TODO: EoR

    suspend fun setup(cognitoToken: String){

        amplifyManager.fetchUserAttributes { amplifyResultValue ->

            fetchUserAttributesHandler(amplifyResultValue)
        }
    }

    fun fetchUserAttributesHandler(amplifyResultValue: AmplifyResultValue){

        if (amplifyResultValue.wasCallSuccessful){

            var attributeUID = ""
            for (attr in amplifyResultValue.attributes!!){

                when (attr.key){
                    AuthUserAttributeKey.custom("sub")-> attributeUID = attr.value
                    AuthUserAttributeKey.email() -> email = attr.value
                    AuthUserAttributeKey.phoneNumber() -> phoneNumber = attr.value
                }
            }

            // Start the rest manager
            restManager.setUserID(attributeUID)
            restManager.setupRESTCommand(
                RESTCmdType.USER,
                mutableMapOf<String, Any>(),
                RESTMethodType.GET,
                this
            )
        } else{

            var errorMessage: String = "Generic fetch user error"

            when(amplifyResultValue.authException?.message){
                "User does not exist" -> errorMessage = "User doesn't exist"
            }

            val response: MutableMap<String, Any> = mutableMapOf()
            response.put("error", errorMessage)

            userManagerDelegate?.umHandleResponse(response, RESTCmdType.NONE, RESTMethodType.NONE)
        }
    }

    fun setDelegate(delegate: UserManagerDelegate){
        this.userManagerDelegate = delegate
    }

    fun updateUserProfile(params: Map<String, String>){

    }

    fun setAmplifyManager(amplifyManager: AmplifyManager){
        this.amplifyManager = amplifyManager
    }

    fun setRESTManager(restManager: RESTManager){
        this.restManager = restManager
    }


    // MARK: RESTManager Delegate functions
    override fun handleResponse(
        response: String,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        // Parse up the response

        var parsedResponse = mutableMapOf<String, String?>()//parseMessage(response)

        if (commandType == RESTCmdType.USER &&
            (methodType == RESTMethodType.POST ||
                    methodType == RESTMethodType.GET ||
                    methodType == RESTMethodType.PATCH
            )
        ){
            Log.i("parseMessage","parseMessage message: $response")
            user = Json.decodeFromString(response)
        } else if (commandType == RESTCmdType.USERIMG && methodType == RESTMethodType.GET){

            restManager.addUserImageToBackend(parsedResponse.getValue("uploadTo")!!)
            return
        }

        userManagerDelegate?.umHandleResponse(
            parsedResponse as MutableMap<String, Any>,
            commandType,
            methodType
        )
    }

    override fun errorReceived(error: String) {

        userManagerDelegate?.umHandleResponse(
            mutableMapOf(Pair("error", error)),
            RESTCmdType.NONE,
            RESTMethodType.NONE
        )
    }

    private fun parseMessage(message: String): Map<String, String?>{

        Log.i("parseMessage","parseMessage message: $message")

        return Json.decodeFromString(message)
    }

    private fun setupUser(response: Map<String, String?>){

        for (pair: Map.Entry<String, String?> in response){

            when (pair.key){
                "userId"-> userID = pair.value!!
                "firstName"-> firstName = pair.value!!
                "lastName"-> lastName = pair.value!!
//                "middleName"-> middleName = pair.value
                "email"-> email = pair.value!!
                "phone"-> phoneNumber = pair.value!!
                "deviceTokens"-> deviceTokens = pair.value!!.split(",").map { it }.toMutableList()
                "stoveMakeModel"-> stoveMakeModel = pair.value!!
                "stoveOrientation"-> stoveOrientation = pair.value!!.toInt()
                "numKnobs"-> numKnobs = pair.value!!.toInt()
                "stoveGasOrElectric"-> stoveType = pair.value!!
                "stoveSetupComplete"-> stoveSetupComplete = pair.value!!.toBoolean()
                "stoveAutoOffMins"-> stoveAutoOffMins = pair.value!!.toInt()
                "uiAppVersion"-> uiAppVersion = pair.value!!
            }
        }
    }

    private fun setupUser(user: UserResponse){

        Log.i("parseMessage","parseMessage message: $user")
    }

    // MARK: WebsocketManagerDelegate
    override fun handleWebsocketResponse(response: MutableMap<String, Any>) {
//        TODO("Not yet implemented")
    }


}