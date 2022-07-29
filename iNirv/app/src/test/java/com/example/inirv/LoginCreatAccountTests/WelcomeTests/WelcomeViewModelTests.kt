package com.example.inirv.LoginCreatAccountTests.WelcomeTests

import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.LoginAndCreateAccount.Welcome.WelcomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class WelcomeViewModelTests {

    @MockK
    lateinit var mock_Coordinator: Coordinator

    lateinit var welcomeViewModel: WelcomeViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)
        welcomeViewModel = WelcomeViewModel(
            onFinished = mock_Coordinator::coordinatorInteractorFinished
        )
    }

    @Test
    fun test_buttonPressed(){

        welcomeViewModel.buttonPressed(true)

        verify { mock_Coordinator.coordinatorInteractorFinished(welcomeViewModel) }
    }
}