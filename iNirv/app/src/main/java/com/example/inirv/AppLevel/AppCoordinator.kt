package com.example.inirv.AppLevel

import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.Interfaces.Navigator

class AppCoordinator(
    override val navigator: Navigator,
    val _goToScreen: AppNavigatorScreen = AppNavigatorScreen.welcome
) : Coordinator {

    var goToScreen: AppNavigatorScreen
        private set

    init {
        goToScreen = _goToScreen
    }

    override fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor) {

//        if (coordinatorInteractor is LaunchViewModel){
//
//            goToScreen = coordinatorInteractor.screen
//        }
//
//        navigator.goToScreen(this)
    }

}

