package com.example.inirv.LoginCreatAccountTests.CreateAccountTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAPassword.CAPasswordViewModel
import com.example.inirv.managers.AmplifyManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CAPasswordVMTests {

    // Allow for livedata to be updated instantly while not on the main thread
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var mockAM: AmplifyManager

    lateinit var caPasswordViewModel: CAPasswordViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this)

        caPasswordViewModel = CAPasswordViewModel(
            firstName = "TestFirst",
            lastName = "TestLast",
            email = "TestEmail",
            phoneNumber = "TestPhone",
            amplifyManager = mockAM
        )
    }

    @Test
    fun test_continueBtnPressedNoPassword(){

        val password: String = ""
        val confirmPassword: String = ""
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to enter a password")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedNoConfirmPassword(){

        val password: String = "testPassword"
        val confirmPassword: String = ""
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to enter a confirmation password")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedPasswordLessThan9(){

        val password: String = "12345678"
        val confirmPassword: String = "123456789"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure that your password length is 9 characters or more")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedConfirmPasswordLessThan9(){

        val password: String = "123456789"
        val confirmPassword: String = "12345678"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure that your confirmation password length is 9 characters or more")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedPasswordMore26(){

        val password: String = "123456789012345678901234567"
        val confirmPassword: String = "123456789"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure that your password length is less than 26 characters")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedConfirmPasswordMore26(){

        val password: String = "123456789"
        val confirmPassword: String = "123456789012345678901234567"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure that your confirmation password length is less than 26 characters")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedDifferentPassword(){

        val password: String = "123456789"
        val confirmPassword: String = "different password"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure that your passwords match")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedUpperLower(){

        val password: String = "TesterTester"
        val confirmPassword: String = "TesterTester"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedUpperNumber(){

        val password: String = "T12345678"
        val confirmPassword: String = "T12345678"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedUpperSpecial(){

        val password: String = "T\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        val confirmPassword: String = "T\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedLowerNumber(){

        val password: String = "t12345678"
        val confirmPassword: String = "t12345678"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedLowerSpecial(){

        val password: String = "t\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        val confirmPassword: String = "t\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    @Test
    fun test_continueBtnPressedNumberSpecial(){

        val password: String = "1\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        val confirmPassword: String = "1\$\$\$\$\$\$\$\$\$\$\$\$\$\$\$"
        caPasswordViewModel.continueBtnPressed(password, confirmPassword)

        assert(caPasswordViewModel.errorMessage.value == "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
        assert(caPasswordViewModel.confMessage.value == null)
    }

    // TODO: Need to finish up these unit tests (CAPasswordVM)
//    @Test
//    fun test_continueBtnPressedSuccess() = runBlocking {
//
//        val password: String = "Testing1234!!!"
//        val confirmPassword: String = "Testing1234!!!"
//
//        coEvery { mockAM.signUp(caPasswordViewModel.email, password, caPasswordViewModel.phoneNumber){}  } returns Unit
//
//        caPasswordViewModel.continueBtnPressed(password, confirmPassword)
//
//        assert(caPasswordViewModel.errorMessage.value == null)
//        assert(caPasswordViewModel.confMessage.value == null)
//
//        coVerify { mockAM.signUp(caPasswordViewModel.email, password, caPasswordViewModel.phoneNumber){} }
//    }

    @Test
    fun test_(){

    }
}