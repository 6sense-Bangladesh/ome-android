package com.example.inirv.LoginCreatAccountTests.LoginTests

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.amazonaws.auth.AWSCredentials
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.AWSCognitoUserPoolTokens
import com.amplifyframework.auth.result.AuthSessionResult
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.LoginAndCreateAccount.LoginFlow.Login.LoginViewModel
import com.example.inirv.managers.*
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LoginViewModelTests {

    @MockK
    lateinit var mock_coordinator: Coordinator

    @MockK
    lateinit var mock_userManager: UserManager
    @MockK
    lateinit var mock_knobManager: KnobManager
    @MockK
    lateinit var mock_amplifyManager: AmplifyManager
    @MockK
    lateinit var mock_sharedPreferences: SharedPreferences


    lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        loginViewModel = LoginViewModel(
            onFinished = mock_coordinator::coordinatorInteractorFinished,
            _userManager = mock_userManager,
            _knobManager = mock_knobManager,
            _amplifyManager = mock_amplifyManager,
            _sharedPreferences = mock_sharedPreferences
        )
    }

    @Test
    fun test_setup(){

        loginViewModel.setup()

        verify { mock_userManager.setDelegate(loginViewModel) }
        verify { mock_knobManager.setDelegate(loginViewModel) }
    }


    @Test
    fun test_processFetchUserAuthSessionResultsUnsuccessful(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false

        loginViewModel.processFetchUserAuthSessionResults(amplifyResultValue)

        assert(loginViewModel.errorMessageLiveData != null)
        assert(loginViewModel.errorMessageLiveData.value ==
                "Something went wrong. Please try again")

    }

    @Test
    fun test_processFetchUserAuthSessionResultsSuccessful() = runBlocking{

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true

        val cognitoToken: String = "TestToken"
        val userPoolTokens = AWSCognitoUserPoolTokens(
            cognitoToken,
            "",
            ""
        )

        val awsCredentials: AWSCredentials? = null
        val authSessionResultAC: AuthSessionResult<AWSCredentials> = AuthSessionResult.success(awsCredentials)
        val authSessionResultUPT: AuthSessionResult<AWSCognitoUserPoolTokens> = AuthSessionResult.success(userPoolTokens)
        val authSessionResultID: AuthSessionResult<String> = AuthSessionResult.success("")
        val authSessionResultUS: AuthSessionResult<String> = AuthSessionResult.success("")

        amplifyResultValue.session = AWSCognitoAuthSession(true,
            authSessionResultID,
            authSessionResultAC,
            authSessionResultUS,
            authSessionResultUPT)

        loginViewModel.processFetchUserAuthSessionResults(amplifyResultValue)

        coVerify {  mock_userManager.setup(cognitoToken) }
    }

    @Test
    fun test_checkLoginInfoNoPassword(){

        val toReturn= loginViewModel.checkLoginInfo("TestEmail@email.com","")

        assert(toReturn ==
                "Please make sure to enter a password")
    }

    @Test
    fun test_checkLoginInfoNoEmail(){

        val toReturn= loginViewModel.checkLoginInfo("","TestPassword")

        assert(toReturn ==
                "Please make sure to enter an email")
    }

    @Test
    fun test_checkLoginInfoValid(){

        val toReturn = loginViewModel.checkLoginInfo("TestEmail@email.com","TestPassword")

        assert(toReturn ==
                "No errors detected")
    }

    @Test
    fun test_processLoginResultsUnsuccessfulDefault(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false
        amplifyResultValue.authException = AuthException("Test Error", "")

        loginViewModel.processLoginResults(amplifyResultValue)

        assert(loginViewModel.errorMessageLiveData.value ==
                "Please make sure your email and password are correct")
    }

    @Test
    fun test_processLoginResultsUnsuccessfulIncorrectCredentials(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = false
        amplifyResultValue.authException = AuthException("Incorrect username or password", "")

        loginViewModel.processLoginResults(amplifyResultValue)

        assert(loginViewModel.errorMessageLiveData.value ==
                "Incorrect username or password")
    }

    @Test
    fun test_processLoginResultsSuccessConfirmSignUp(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true
        amplifyResultValue.message = "Confirm signup"

        loginViewModel.processLoginResults(amplifyResultValue)

        assert(loginViewModel.goToConfirmation)
        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }
    }

//    @Test
//    fun test_processLoginResultsSuccessDone()= runBlocking{
//
//        val amplifyResultValue = AmplifyResultValue()
//        amplifyResultValue.wasCallSuccessful = true
//        amplifyResultValue.message = "Done"
//
//        loginViewModel.processLoginResults(amplifyResultValue)
//
//        coVerify{ mock_amplifyManager.fetchAuthSession{amplifyResultValue ->  } }
//    }

    @Test
    fun test_processLoginResultsSuccessDefault(){

        val amplifyResultValue = AmplifyResultValue()
        amplifyResultValue.wasCallSuccessful = true
        amplifyResultValue.message = ""

        loginViewModel.processLoginResults(amplifyResultValue)

        assert(loginViewModel.errorMessageLiveData.value ==
                "Please make sure your email and password are correct")
    }

    @Test
    fun test_forgotPasswordFlow(){

        loginViewModel.forgotPasswordFlow()

        assert(loginViewModel.actionPressed)

        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }
    }

    @Test
    fun test_umHandleResponseError() {

//        verify { mock_sharedPreferences.edit().putBoolean("appConfigure", false) }
        val errorMsg: String = "TestError"
        val response: MutableMap<String, Any> = mutableMapOf()
        response.put("error", errorMsg)
        val commandType: RESTCmdType = RESTCmdType.NONE
        val methodType: RESTMethodType = RESTMethodType.NONE

        loginViewModel.umHandleResponse(response, commandType, methodType)

        assert(loginViewModel.errorMessageLiveData.value == errorMsg)
    }

    @Test
    fun test_umHandleResponseNoStoveOrientation(){

        val response: MutableMap<String, Any> = mutableMapOf()
        val commandType: RESTCmdType = RESTCmdType.USER
        val methodType: RESTMethodType = RESTMethodType.GET

        every { mock_userManager.stoveOrientation } returns -1

        loginViewModel.umHandleResponse(response, commandType, methodType)

        verify { mock_sharedPreferences.edit().putBoolean("appConfigure", false) }
        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }

        assert(loginViewModel.loginSuccess)
    }

    @Test
    fun test_umHandleResponseNoKnobs(){

        val response: MutableMap<String, Any> = mutableMapOf()
        val commandType: RESTCmdType = RESTCmdType.USER
        val methodType: RESTMethodType = RESTMethodType.GET

        every { mock_userManager.stoveOrientation } returns 2
        every { mock_userManager.numKnobs } returns 0

        loginViewModel.umHandleResponse(response, commandType, methodType)

        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }

        assert(loginViewModel.loginSuccess)
    }

    @Test
    fun test_umHandleResponseHasKnobs(){

        val response: MutableMap<String, Any> = mutableMapOf()
        val commandType: RESTCmdType = RESTCmdType.USER
        val methodType: RESTMethodType = RESTMethodType.GET

        every { mock_userManager.stoveOrientation } returns 2
        every { mock_userManager.numKnobs } returns 1

        loginViewModel.umHandleResponse(response, commandType, methodType)

        verify { mock_knobManager.setup() }
        verify { mock_knobManager.setDelegate(loginViewModel) }
    }

    @Test
    fun test_kmHandleResponseAllKnobsGetSetup(){

        val response: MutableMap<String, Any> = mutableMapOf()
        val commandType: RESTCmdType = RESTCmdType.ALLKNOBS
        val methodType: RESTMethodType = RESTMethodType.GET

        every { mock_userManager.numKnobs } returns 0

        loginViewModel.kmHandleResponse(response, commandType, methodType)

        assert(loginViewModel.loginSuccess)

        verify { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }
    }

    @Test
    fun test_kmHandleResponseAllKnobsGetHasKnobs(){

        val response: MutableMap<String, Any> = mutableMapOf()
        val commandType: RESTCmdType = RESTCmdType.ALLKNOBS
        val methodType: RESTMethodType = RESTMethodType.GET

        every { mock_userManager.numKnobs } returns 1

        loginViewModel.kmHandleResponse(response, commandType, methodType)

        assert(loginViewModel.loginSuccess)

        verify(inverse = true) { mock_sharedPreferences.edit().putBoolean("setupFirstDevice", true) }
        verify { mock_coordinator.coordinatorInteractorFinished(loginViewModel) }
    }

}