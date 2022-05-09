package com.example.inirv

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

        var handleWebSocketResponseCalled: Int = 0
        var handleWebSocketResponseResponse: MutableMap<String, Any> = mutableMapOf<String, Any>()
        override fun handleWebSocketResponse(response: MutableMap<String, Any>) {
            handleWebSocketResponseCalled += 1
            handleWebSocketResponseResponse = response
        }

        var errorReceivedCalled: Int = 0
        var errorReceivedError: String = ""
        override fun errorReceived(error: String) {
            errorReceivedCalled += 1
            errorReceivedError = error
        }
    }

    lateinit var restManager: RESTManager
    lateinit var mockRMD: MockRESTManagerDelegate

    @Before
    fun setup(){

        restManager = RESTManager()
        mockRMD = MockRESTManagerDelegate()
    }

    @After
    fun tearDown(){

    }

    @Test
    fun test_setupRESTCommandNoUserID() {

        restManager.setupRESTCommand(RESTCmdType.NONE, mutableMapOf<String, Any>(), mockRMD)

        // Check for error
        assert(mockRMD.errorReceivedCalled == 1)
        assert(mockRMD.errorReceivedError == "No user ID for the REST API Call")
    }

}