package com.example.inirv.ManagerTests

import com.example.inirv.managers.*
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class KnobManagerTests {

    @MockK
    lateinit var mock_restManager: RESTManager

    @MockK
    lateinit var mock_knobManagerDelegate: KnobManagerDelegate

    @MockK
    lateinit var mock_websocketManager: WebsocketManager

    lateinit var knobManager: KnobManager

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        knobManager = KnobManager
        knobManager.setRESTManager(mock_restManager)
        knobManager.setDelegate(mock_knobManagerDelegate)
        knobManager.setWebsocketManager(mock_websocketManager)
    }

    @Test
    fun test_handleResponseALLKnobs(){

//        val response = "{\"userId\":\"TestUID\"}"
//        val responseMap = mutableMapOf<String, Any>(Pair("userId", "TestUID"))
//
//        knobManager.handleResponse(response, RESTCmdType.USER, RESTMethodType.POST)
//
//        verify { mock_knobManagerDelegate.kmHandleResponse(responseMap, RESTCmdType.USER, RESTMethodType.POST) }
    }

    @Test
    fun test_sendRestCommand(){

        val params: MutableMap<String, Any> = mutableMapOf(Pair("TestKey", "TestValue"))
        val command: RESTCmdType = RESTCmdType.ALLKNOBS
        val method: RESTMethodType = RESTMethodType.GET

        knobManager.sendRestCommand(params, command, method)

        verify { mock_restManager.setupRESTCommand(command, params, method, knobManager) }
    }

    @Test
    fun test_handleResponseSafetyLockOn(){


    }

    @Test
    fun test_handleResponseSafetyLockOff(){

    }

    @Test
    fun test_handleResponseKnobPatch(){

    }

    @Test
    fun test_handleResponseKnobGet(){

    }

    @Test
    fun test_handleResponseOwnershipGetAndKnobPost(){

    }

    @Test
    fun test_handleResponseInitCalibPost(){

    }

    @Test
    fun test_handleResponse(){

    }

    @Test
    fun test_(){

    }
}