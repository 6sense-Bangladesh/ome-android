package com.example.inirv.LoginCreatAccountTests.CreateAccountTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName.CaNameViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CANameTests {

    @get:Rule
    val rule = InstantTaskExecutorRule()


    lateinit var caNameViewModel: CaNameViewModel

    @Before
    fun setup(){

        caNameViewModel = CaNameViewModel()
    }

    @Test
    fun test_continueBtnPressedNoFirstName(){

        caNameViewModel.continueBtnPressed("","")

        assert(caNameViewModel.errorMessage.value == "Please make sure to enter a first name")
    }

    @Test
    fun test_continueBtnPressedNoLastName(){

        caNameViewModel.continueBtnPressed("TestFirst","")

        assert(caNameViewModel.errorMessage.value == "Please make sure to enter a last name")
    }

    @Test
    fun test_continueBtnPressedAllNecessaryFieldsMet(){

        val testFirstName: String = "TestFirst"
        val testLastName: String = "TestLast"
        caNameViewModel.continueBtnPressed(testFirstName,testLastName)

        assert(caNameViewModel.errorMessage.value == null)
        assert(caNameViewModel.firstName.value == testFirstName)
        assert(caNameViewModel.lastName.value == testLastName)
    }
}