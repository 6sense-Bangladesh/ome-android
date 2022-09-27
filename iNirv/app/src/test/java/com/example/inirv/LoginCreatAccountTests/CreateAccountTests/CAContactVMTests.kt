package com.example.inirv.LoginCreatAccountTests.CreateAccountTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAContact.CAContactViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CAContactVMTests {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var caContactViewModel: CAContactViewModel

    @Before
    fun setup(){

        caContactViewModel = CAContactViewModel()
    }

    @Test
    fun test_continueBtnPressedNoEmail(){

        val email: String = ""
        val phoneNumber: String = ""

        caContactViewModel.continueBtnPressed(email, phoneNumber)

        assert(caContactViewModel.errorMessage.value == "Please make sure to enter an email")
    }

    @Test
    fun test_continueBtnPressedInvalidEmail(){

        val email: String = "test@test"
        val phoneNumber: String = ""

        caContactViewModel.continueBtnPressed(email, phoneNumber)

        assert(caContactViewModel.errorMessage.value == "Please make sure youre using a valid email")
    }

    @Test
    fun test_continueBtnPressedSuccess(){

        val email: String = "test@test.com"
        val phoneNumber: String = "555-555-5555"

        caContactViewModel.continueBtnPressed(email, phoneNumber)

        assert(caContactViewModel.errorMessage.value == null)
        assert(caContactViewModel.email.value == email)
        assert(caContactViewModel.phoneNumber == phoneNumber)
    }
}