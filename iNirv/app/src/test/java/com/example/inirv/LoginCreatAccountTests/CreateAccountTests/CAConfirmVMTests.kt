package com.example.inirv.LoginCreatAccountTests.CreateAccountTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAConfirm.CAConfirmViewModel
import com.example.inirv.TestHelpers.MainDispatcherRule
import com.example.inirv.managers.AmplifyManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CAConfirmVMTests {

    // Allow for livedata to be updated instantly while not on the main thread
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var mockAM: AmplifyManager

    lateinit var caConfirmViewModel: CAConfirmViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this)

        caConfirmViewModel = CAConfirmViewModel(
            amplifyManager = mockAM
        )
    }

    @Test
    fun test_onStartConfCodeSent(){

        val censoredEmail: String = "censoredEmail"

        caConfirmViewModel = CAConfirmViewModel(censoredEmail = censoredEmail)
        caConfirmViewModel.onStart()


        assert(caConfirmViewModel.confirmWasSentMsg.value == "Confirmation code sent via email to: $censoredEmail \nMake sure to check your spam folder")
    }

    @Test
    fun test_onStartNotConfCodeSent() = runBlocking {

        val email: String = "Test@test.com"
        caConfirmViewModel = CAConfirmViewModel(
            amplifyManager = mockAM,
            wasConfirmationCodeSent = false,
            email = email
        )

        coEvery {
            mockAM.resendSignUpCode(email){}
        } returns Unit

        caConfirmViewModel.onStart()

        coVerify {
            mockAM.resendSignUpCode(email){}
        }

        assert(caConfirmViewModel.confirmWasSentMsg.value == null)
    }

    @Test
    fun test_resendConfCode(){

    }

    @Test
    fun test_confirmPressedEmptyConfirmation(){

        val confirmationCode: String = ""
        val errorMessage: String = "Please make sure to enter a confirmation code."

        caConfirmViewModel.confirmPressed(confirmationCode)

        assert(caConfirmViewModel.errorMessage.value == errorMessage)
    }

    @Test
    fun test_confirmPressed(){

    }

    @Test
    fun test_(){

    }
}