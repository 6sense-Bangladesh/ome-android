package com.example.inirv.LoginCreatAccountTests.LoginTests

import com.example.inirv.Interfaces.Navigator
import com.example.inirv.LoginAndCreateAccount.LoginFlow.Login.LoginCoordinator
import com.example.inirv.LoginAndCreateAccount.LoginFlow.Login.LoginGoToScreens
import com.example.inirv.LoginAndCreateAccount.LoginFlow.Login.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoginCoordinatorTests {

    @MockK
    lateinit var navigator: Navigator

    lateinit var loginCoordinator: LoginCoordinator

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)
        loginCoordinator = LoginCoordinator(navigator)
    }

    @Test
    fun test_coordinatorInteractorFinishedLoginPressed(){

        val mock_viewModel: LoginViewModel = mockk<LoginViewModel>(relaxed = true)

        every { mock_viewModel.loginSuccess } returns true

        loginCoordinator.coordinatorInteractorFinished(mock_viewModel)

        verify { navigator.goToScreen(loginCoordinator) }
        assert(loginCoordinator.screen == LoginGoToScreens.parentNavigator)
    }

    @Test
    fun test_coordinatorInteractorFinishedGoToConfirmation(){

        val mock_viewModel: LoginViewModel = mockk<LoginViewModel>(relaxed = true)

        every { mock_viewModel.goToConfirmation } returns true

        loginCoordinator.coordinatorInteractorFinished(mock_viewModel)

        verify { navigator.goToScreen(loginCoordinator) }
        assert(loginCoordinator.screen == LoginGoToScreens.caConfirm)
    }

    @Test
    fun test_coordinatorInteractorFinishedForgotPassword(){

        val mock_viewModel: LoginViewModel = mockk<LoginViewModel>(relaxed = true)

        every { mock_viewModel.forgotPasswordButtonPressed } returns true

        loginCoordinator.coordinatorInteractorFinished(mock_viewModel)

        verify { navigator.goToScreen(loginCoordinator) }
        assert(loginCoordinator.screen == LoginGoToScreens.forgotPassword)
    }
}