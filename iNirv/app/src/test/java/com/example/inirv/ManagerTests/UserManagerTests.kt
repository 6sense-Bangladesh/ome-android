package com.example.inirv.ManagerTests

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.example.inirv.managers.*
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UserManagerTests {

    @MockK
    lateinit var mock_restManager: RESTManager

    @MockK
    lateinit var mock_delegate: UserManagerDelegate

    @MockK
    lateinit var mock_amplifyManager: AmplifyManager

    lateinit var userManager: UserManager

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        userManager = UserManager
        userManager.setAmplifyManager(mock_amplifyManager)
        userManager.setDelegate(mock_delegate)
        userManager.setRESTManager(mock_restManager)
    }

    @Test
    fun test_fetchUserAttributesHandlerFailureUserDoesntExist(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false
        amplifyResultValue.authException = AuthException("User does not exist", "")

        userManager.fetchUserAttributesHandler(amplifyResultValue)

        verify { mock_delegate.umHandleResponse(
            mutableMapOf<String, Any>(
                Pair("error", "User doesn't exist")
            ),
            RESTCmdType.NONE,
            RESTMethodType.NONE)
        }
    }

    @Test
    fun test_fetchUserAttributesHandlerFailureGenericError(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false
        amplifyResultValue.authException = AuthException("TEST Exception", "")

        userManager.fetchUserAttributesHandler(amplifyResultValue)

        verify { mock_delegate.umHandleResponse(
            mutableMapOf<String, Any>(
                Pair("error", "Generic fetch user error")
            ),
            RESTCmdType.NONE,
            RESTMethodType.NONE)
        }
    }

    @Test
    fun test_fetchUserAttributesHandler(){

        val testEmail: String = "testEmail"
        val testPhone: String = "testPhoneNumber"
        val testUID: String = "testUID"
        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true
        amplifyResultValue.attributes = listOf(
            AuthUserAttribute(AuthUserAttributeKey.email(), testEmail),
            AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), testPhone),
            AuthUserAttribute(AuthUserAttributeKey.custom("sub"), testUID)
        )

        userManager.fetchUserAttributesHandler(amplifyResultValue)

        verify { mock_restManager.setUserID(testUID) }
        verify { mock_restManager.setupRESTCommand(
            RESTCmdType.USER,
            mutableMapOf<String, Any>(),
            RESTMethodType.GET,
            userManager)
        }

        assert(userManager.email == testEmail)
        assert(userManager.phoneNumber == testPhone)
    }

    @Test
    fun test_handleResponseUserPost(){

        val response = "{\"userId\":\"TestUID\"}"
        val responseMap = mutableMapOf<String, Any>(Pair("userId", "TestUID"))

        userManager.handleResponse(response, RESTCmdType.USER, RESTMethodType.POST)

        verify { mock_delegate.umHandleResponse(responseMap, RESTCmdType.USER, RESTMethodType.POST) }

        assert(userManager.userID == "TestUID")
    }

    @Test
    fun test_handleResponseUserImage(){

        val uploadTo = "TestURL"
        val response = "{\"uploadTo\":\"$uploadTo\"}"
        val responseMap = mutableMapOf<String, Any>(Pair("userId", "TestUID"))

        userManager.handleResponse(response, RESTCmdType.USERIMG, RESTMethodType.GET)

        verify { mock_restManager.addUserImageToBackend(uploadTo) }
    }

    @Test
    fun test_(){

    }

}