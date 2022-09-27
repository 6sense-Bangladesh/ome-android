package com.example.inirv.LoginCreatAccountTests.CreateAccountTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CASuccess.CASuccessViewModel
import com.example.inirv.managers.AmplifyManager
import com.example.inirv.managers.RESTCmdType
import com.example.inirv.managers.RESTMethodType
import com.example.inirv.managers.UserManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CASuccessVMTests {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var mockUserManager: UserManager

    @MockK
    lateinit var mockAmplifyManager: AmplifyManager

    lateinit var caSuccessViewModel: CASuccessViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this)

        caSuccessViewModel = CASuccessViewModel(
            "",
            "",
            mockUserManager,
            mockAmplifyManager
        )
    }

    // TODO: Need to look into unit testing escaping functions/lambdas (amplifyManager.signIn)
//    @Test
//    fun test_startSetupBtnPressed(){
//
//    }

    @Test
    fun test_umHandleResponse(){

        val testResponse: MutableMap<String, Any> = mutableMapOf()
        val testCmd = RESTCmdType.USER
        val testMethod = RESTMethodType.GET

        caSuccessViewModel.umHandleResponse(
            response = testResponse,
            commandType = testCmd,
            methodType = testMethod
        )

        assert(caSuccessViewModel.userManagerSetupComplete.value == true)
    }

}