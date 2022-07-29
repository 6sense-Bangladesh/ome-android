package com.example.inirv.AppLevelTests

import android.content.SharedPreferences
import com.amazonaws.auth.AWSCredentials
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.AWSCognitoUserPoolTokens
import com.amplifyframework.auth.result.AuthSessionResult
import com.example.inirv.AppLevel.AppNavigatorScreen
import com.example.inirv.AppLevel.ui.launch.LaunchViewModel
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.managers.*
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LaunchViewModelTests {


    @MockK
    lateinit var mockUserManager: UserManager
    @MockK
    lateinit var mockKnobManager: KnobManager
    @MockK
    lateinit var mockAmplifyManager: AmplifyManager
    @MockK
    lateinit var mockCoordinator: Coordinator
    @MockK
    lateinit var mock_sharedPreferences: SharedPreferences

    lateinit var systemUnderTest: LaunchViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        systemUnderTest = LaunchViewModel(
            _userManager = mockUserManager,
            _knobManager = mockKnobManager,
            _amplifyManager = mockAmplifyManager,
            onFinished = mockCoordinator::coordinatorInteractorFinished,
            _sharedPreferences = mock_sharedPreferences
        )
    }

    @Test
    fun test_setup(){

        systemUnderTest.setup()

        verify { mockUserManager.setDelegate(systemUnderTest) }
        verify { mockKnobManager.setDelegate(systemUnderTest) }
    }

    @Test
    fun test_processCheckSignInStatusResultsSignedIn() = runBlocking{

        val resultValue = AmplifyResultValue()
        val accessTokenString = "Test Access Token"
        val userPoolTokens = AWSCognitoUserPoolTokens(
            accessTokenString,
            "",
            ""
        )

        val awsCredentials: AWSCredentials? = null
        val authSessionResultAC: AuthSessionResult<AWSCredentials> = AuthSessionResult.success(awsCredentials)
        val authSessionResultUPT: AuthSessionResult<AWSCognitoUserPoolTokens> = AuthSessionResult.success(userPoolTokens)
        val authSessionResultID: AuthSessionResult<String> = AuthSessionResult.success("")
        val authSessionResultUS: AuthSessionResult<String> = AuthSessionResult.success("")
        resultValue.wasCallSuccessful = true

        resultValue.session = AWSCognitoAuthSession(true,
            authSessionResultID,
            authSessionResultAC,
            authSessionResultUS,
            authSessionResultUPT)

        systemUnderTest.processCheckSignInStatusResults(resultValue)

        verify { mock_sharedPreferences.edit().putBoolean("appConfigure", true) }
        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", false) }
        coVerify { mockUserManager.setup(accessTokenString) }
        verify(inverse = true) { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
    }

    @Test
    fun test_processCheckSignInStatusResultsNotSignedIn(){

        val resultValue = AmplifyResultValue()
        resultValue.wasCallSuccessful = true
        resultValue.session = AuthSession(false)

        systemUnderTest.processCheckSignInStatusResults(resultValue)

        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
    }

    @Test
    fun test_processCheckSignInStatusResultsNotSuccessfulCall(){

    }

    @Test
    fun test_signUserOutHandlerSuccess(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true

        systemUnderTest.signUserOutHandler(amplifyResultValue)

        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
    }

    @Test
    fun test_signUserOutHandlerFailure(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false

        systemUnderTest.signUserOutHandler(amplifyResultValue)

        // TODO:  Need To add tests for this
    }

    @Test
    fun test_kmHandleResponseStoveSetupComplete(){

        val response: MutableMap<String, Any> = mutableMapOf()

        every { mockUserManager.numKnobs } returns 0
        every { mockUserManager.stoveSetupComplete } returns  true
        systemUnderTest.kmHandleResponse(response, RESTCmdType.TEST, RESTMethodType.PATCH)

        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.knobInstallationOne)
    }

    @Test
    fun test_kmHandleResponseStoveSetupNotComplete(){

        val response: MutableMap<String, Any> = mutableMapOf()

        every { mockUserManager.numKnobs } returns 0
        every { mockUserManager.stoveSetupComplete } returns  false
        systemUnderTest.kmHandleResponse(response, RESTCmdType.TEST, RESTMethodType.PATCH)

        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.home)
    }

    @Test
    fun test_kmHandleResponseOneOrMoreKnobs(){

        val response: MutableMap<String, Any> = mutableMapOf()

        every { mockUserManager.numKnobs } returns 1
        every { mockUserManager.stoveSetupComplete } returns  true
        systemUnderTest.kmHandleResponse(response, RESTCmdType.TEST, RESTMethodType.PATCH)

        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.home)
    }

//    @Test
//    fun test_umHandleResponseErrorUserDoesntExist(){
//
//        val spyLaunchViewModel = Mockito.spy(systemUnderTest)
//        val response: MutableMap<String, Any> = mutableMapOf()
//        response.put("error", "User doesn't exist")
//
//        spyLaunchViewModel.umHandleResponse(response, RESTCmdType.TEST, RESTMethodType.NONE)
//
//        verify { spyLaunchViewModel.signUserOut() }
//    }
//
//    @Test
//    fun test_umHandleResponseErrorGenericFetchUserError(){
//
//        val response: MutableMap<String, Any> = mutableMapOf()
//        response.put("error", "Generic fetch user error")
//
//        systemUnderTest.umHandleResponse(response, RESTCmdType.TEST, RESTMethodType.NONE)
//
//        verify { systemUnderTest.signUserOut() }
//        verify(inverse = true) { systemUnderTest.userFlowCheck() }
//    }
//
//    @Test
//    fun test_umHandleResponseUserGet(){
//
//        val spyLaunchViewModel = systemUnderTest
//        val response: MutableMap<String, Any> = mutableMapOf()
//        response.put("error", "Generic fetch user error")
//
//        systemUnderTest.umHandleResponse(
//            mutableMapOf<String, Any>(),
//            RESTCmdType.USER,
//            RESTMethodType.GET
//        )
//
//        verify { spyLaunchViewModel.userFlowCheck() }
////        verify(inverse = true) { systemUnderTest.signUserOut() }
//    }

    @Test
    fun test_userFlowCheckUpdateUserToken(){

        val deviceToken = "TestDeviceToken"
        every {
            mock_sharedPreferences.getString("omePreferences", "deviceToken")
        } returns deviceToken
        every {
            mockUserManager.deviceTokens
        } returns mutableListOf()

        systemUnderTest.userFlowCheck()

        var params = mutableMapOf<String, String>()
        params.put("deviceTokens", deviceToken)

        verify {
            mockUserManager.updateUserProfile(params)
        }
    }

    @Test
    fun test_userFlowCheckNegativeOneStoveOrientation(){

        every { mockUserManager.stoveOrientation } returns -1

        systemUnderTest.userFlowCheck()

        verify { mock_sharedPreferences.edit().putBoolean("appConfigure", false) }
        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.stoveBrand)
    }

    @Test
    fun test_userFlowCheckNumKnobsLessThanOneStoveSetupNotComplete(){

        every { mockUserManager.stoveOrientation } returns 2
        every { mockUserManager.numKnobs } returns 0
        every { mockUserManager.stoveSetupComplete } returns true

        systemUnderTest.userFlowCheck()

        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.knobInstallationOne)

    }

    @Test
    fun test_userFlowCheckNumKnobsLessThanOneStoveSetupComplete(){

        every { mockUserManager.stoveOrientation } returns 2
        every { mockUserManager.numKnobs } returns 0
        every { mockUserManager.stoveSetupComplete } returns false

        systemUnderTest.userFlowCheck()

        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mockCoordinator.coordinatorInteractorFinished(systemUnderTest) }
        assert(systemUnderTest.screen == AppNavigatorScreen.home)
    }

    @Test
    fun test_userFlowCheckAtleastOneKnob(){

        every { mockUserManager.stoveOrientation } returns 2
        every { mockUserManager.numKnobs } returns 1

        systemUnderTest.userFlowCheck()

        verify { mockKnobManager.setDelegate(systemUnderTest) }
        verify { mockKnobManager.setup() }
    }

    @Test
    fun test_(){

    }
}