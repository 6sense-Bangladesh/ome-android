package com.example.inirv.LoginAndCreateAccount.LoginFlow.Login

import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.Interfaces.Navigator

class LoginCoordinator(
    override val navigator: Navigator,
    _screen: LoginGoToScreens = LoginGoToScreens.forgotPassword
) : Coordinator {

    var screen: LoginGoToScreens
        private set

    init {
        screen = _screen
    }

    override fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor) {

        // Determine what to do based on the coordinator interactor
        if (coordinatorInteractor is LoginViewModel){

            if (coordinatorInteractor.loginSuccess){
                screen = LoginGoToScreens.parentNavigator
            } else if (coordinatorInteractor.goToConfirmation){
                screen = LoginGoToScreens.caConfirm
            } else if (coordinatorInteractor.forgotPasswordButtonPressed){
                screen = LoginGoToScreens.forgotPassword
            }
        }

        navigator.goToScreen(this)
    }
}