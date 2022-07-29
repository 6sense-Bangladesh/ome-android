package com.example.inirv

import com.example.inirv.managers.*
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class RESTManagerTests {

    class MockRESTManagerDelegate: RESTManagerDelegate{

        var handleResponseCalled: Int = 0
        var handleResponseResponse: String = ""
        var handleResponseCmd: RESTCmdType = RESTCmdType.NONE
        var handleResponseMeth: RESTMethodType = RESTMethodType.NONE
        override fun handleResponse(
            response: String,
            commandType: RESTCmdType,
            methodType: RESTMethodType
        ) {
            handleResponseCalled += 1
            handleResponseResponse = response
            handleResponseCmd = commandType
            handleResponseMeth = methodType
        }

//        var handleWebSocketResponseCalled: Int = 0
//        var handleWebSocketResponseResponse: MutableMap<String, Any> = mutableMapOf<String, Any>()
//        fun handleWebSocketResponse(response: MutableMap<String, Any>) {
//            handleWebSocketResponseCalled += 1
//            handleWebSocketResponseResponse = response
//        }

        var errorReceivedCalled: Int = 0
        var errorReceivedError: String = ""
        override fun errorReceived(error: String) {
            errorReceivedCalled += 1
            errorReceivedError = error
        }
    }

//    class MockAmplifyManager: AmplifyManager(){
//
//
//    }

    lateinit var restManager: RESTManager
    lateinit var mockRMD: MockRESTManagerDelegate
//    lateinit var mockAM: AmplifyManager

//    @MockK
//    lateinit var restManagerDelegate: RESTManagerDelegate

    @MockK
    lateinit var mockAM: AmplifyManager

    @Before
    fun setup(){

        MockKAnnotations.init(this)

//        mockAM = MockAmplifyManager()
        restManager = RESTManager
        mockRMD = MockRESTManagerDelegate()
    }

    @After
    fun tearDown(){

    }

    @Test
    fun test_setupRESTCommandNoUserID() {

        restManager.setupRESTCommand(RESTCmdType.NONE, mutableMapOf<String, Any>(), RESTMethodType.NONE, mockRMD)

        // Check for error
        assert(mockRMD.errorReceivedCalled == 1)
        assert(mockRMD.errorReceivedError == "No user ID for the REST API Call")
    }

    @Test
    fun test_generatePathTest(){

        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        val testResult: String = restManager.generatePath(RESTCmdType.TEST, params)

        assert(testResult == "hello")
    }

    @Test
    fun test_generatePathUser(){

        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        val testResult: String = restManager.generatePath(RESTCmdType.USER, params)

        assert(testResult == "user")
    }

    @Test
    fun test_generatePathUserImage(){

        val fileName = "TestFileName"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["fileName"] = fileName
        val testResult: String = restManager.generatePath(RESTCmdType.USERIMG, params)

        assert(testResult == "user/uploadImageUrl?fileName=${params.getValue("fileName")}")
    }

    @Test
    fun test_generatePathKnob(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.KNOB, params)

        assert(testResult == "knob/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathKnobs(){

        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        val testResult: String = restManager.generatePath(RESTCmdType.ALLKNOBS, params)

        assert(testResult == "knobs")
    }

    @Test
    fun test_generatePathKnobAngle(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.KNOBANGLE, params)

        assert(testResult == "knob/newLevel/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathSchedule(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.SCHED, params)

        assert(testResult == "knob/schedule/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathStartSchedule(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.STARTSCHED, params)

        assert(testResult == "knob/schedule/${params.getValue("macID")}/start")
    }

    @Test
    fun test_generatePathStopSchedule(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.STOPSCHED, params)

        assert(testResult == "knob/schedule/${params.getValue("macID")}/stop")
    }

    @Test
    fun test_generatePathSafetyLockOn(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.SAFETYLOCKON, params)

        assert(testResult == "knob/safetyLock/${params.getValue("macID")}/on")
    }

    @Test
    fun test_generatePathSafetyLockOff(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.SAFETYLOCKOFF, params)

        assert(testResult == "knob/safetyLock/${params.getValue("macID")}/off")
    }

    @Test
    fun test_generatePathClearWifi(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.CLEARWIFI, params)

        assert(testResult == "knob/clearWifi/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathSetCalibration(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.SETCALIBRATION, params)

        assert(testResult == "knob/calibration/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathOwnership(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.OWNERSHIP, params)

        assert(testResult == "knob/ownership/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathPauseSchedule(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.PAUSESCHED, params)

        assert(testResult == "knob/schedule/${params.getValue("macID")}/pause")
    }

    @Test
    fun test_generatePathInitiateCalibration(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.INITCALIB, params)

        assert(testResult == "knob/initCalibration/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathResetAutoShutOffTimer(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.RESETAUTOSHUTOFFTIMER, params)

        assert(testResult == "knob/refreshAutoOff/${params.getValue("macID")}")
    }

    @Test
    fun test_generatePathAndroidInfo(){

        val macID = "TestMACID"
        val params: MutableMap<String, Any> = mutableMapOf<String, Any>()
        params["macID"] = macID
        val testResult: String = restManager.generatePath(RESTCmdType.ANDROIDINFO, params)

        assert(testResult == "iosAppInfo")
    }

    @Test
    fun test_generateMethodGet(){

        val testResult = restManager.generateMethod(RESTMethodType.GET)

        assert(testResult == "GET")
    }

    @Test
    fun test_generateMethodPost(){

        val testResult = restManager.generateMethod(RESTMethodType.POST)

        assert(testResult == "POST")
    }

    @Test
    fun test_generateMethodDelete(){

        val testResult = restManager.generateMethod(RESTMethodType.DELETE)

        assert(testResult == "DELETE")
    }

    @Test
    fun test_generateMethodPatch(){

        val testResult = restManager.generateMethod(RESTMethodType.PATCH)

        assert(testResult == "PATCH")
    }

    @Test
    fun test_getAuthAccessToken() = runBlocking{
// TODO: Figure out how to test out this function
//        var fetchAuthSessionCalled: Int = 0
//        var toReturn = mockk<() -> Unit>()//AmplifyResultValue()
//        val cmdType = RESTCmdType.TEST
//        val params = mutableMapOf<String, Any>()
//        val methodType = RESTMethodType.NONE
//
//        coEvery { mockAM.fetchAuthSession{ } } returns toReturn
//
//        restManager.getAuthAccessToken(cmdType, params, methodType, mockRMD)
//
//        coVerify { mockAM.fetchAuthSession {  } }
//        coVerify { restManager.parseFetchAuthSessionResponse(toReturn, cmdType, params, methodType, mockRMD) }
    }

    // TODO: Need to set up these unit tests
    @Test
    fun test_parseFetchAuthSessionResponse_Success(){

    }

    @Test
    fun test_parseFetchAuthSessionResponse_Failure(){

    }

    @Test
    fun test_connectToWebsocket(){

    }

    @Test
    fun test_sendRESTCommand(){

    }


}