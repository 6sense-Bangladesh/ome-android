package com.example.inirv.LoginAndCreateAccount.Welcome

import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.Interfaces.Navigator

class WelcomeCoordinator(
    override val navigator: Navigator,
    _screen: WelcomeGoToScreens = WelcomeGoToScreens.login
) : Coordinator {

    var screen: WelcomeGoToScreens
        private set

    init {
        screen = _screen
    }

    fun start() {
        var welcomePresenter: WelcomePresenter = WelcomePresenter(
            onFinished =  this::coordinatorInteractorFinished
        )
    }

    override fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor) {

//        // Check what action to take depending on the coordinatorInteractor
//        if (coordinatorInteractor is WelcomeViewModel){
//
//            screen = if (coordinatorInteractor.loginButtonPressed){
//                WelcomeGoToScreens.login
//            } else {
//                WelcomeGoToScreens.createAccountName
//            }
//        }
//
//        navigator.goToScreen(this)
    }
}