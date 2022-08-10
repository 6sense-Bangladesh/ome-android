package com.example.inirv.LoginCreatAccountTests.WelcomeTests

import com.example.inirv.Interfaces.Navigator
import com.example.inirv.LoginAndCreateAccount.Welcome.WelcomeCoordinator
import com.example.inirv.LoginAndCreateAccount.Welcome.WelcomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class WelcomeCoordinatorTests {

    @MockK
    lateinit var navigator: Navigator

    lateinit var welcomeCoordinator: WelcomeCoordinator

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)
        welcomeCoordinator = WelcomeCoordinator(navigator)
    }

    @Test
    fun test_coordinatorInteractorFinishedLogin(){

        val mock_WelcomeViewModel: WelcomeViewModel = mockk<WelcomeViewModel>()

        every { mock_WelcomeViewModel.loginButtonPressed.value } returns true

        welcomeCoordinator.coordinatorInteractorFinished(mock_WelcomeViewModel)

        verify { navigator.goToScreen(welcomeCoordinator) }
    }

    @Test
    fun test_coordinatorInteractorFinishedCreateAccount(){

        val mock_WelcomeViewModel: WelcomeViewModel = mockk<WelcomeViewModel>()

        every { mock_WelcomeViewModel.loginButtonPressed.value } returns false

        welcomeCoordinator.coordinatorInteractorFinished(mock_WelcomeViewModel)

        verify { navigator.goToScreen(welcomeCoordinator) }
    }
}