package com.example.inirv.LoginAndCreateAccount.Welcome

import androidx.fragment.app.FragmentActivity
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator

enum class WelcomeGoToScreens{
    login,
    createAccountName
}

class WelcomeNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) : Navigator {


    init {

    }

    override fun start() {

//        val coordinator = WelcomeCoordinator(
//            navigator = this
//        )
//        val viewModel = WelcomeViewModel(
//            onFinished= coordinator::coordinatorInteractorFinished)
//        activity?.supportFragmentManager?.beginTransaction()?.replace(
//            R.id.container
//            , WelcomeFragment.newInstance(viewModel, viewModel)
//        )
//            ?.commitNow()
    }

    override fun goToScreen(coordinator: Coordinator) {

        var navigator: Navigator? = null

        if (coordinator is WelcomeCoordinator){

//            navigator = if (coordinator.screen ==WelcomeGoToScreens.login){
//                LoginNavigator(this.activity, this::childNavigatorFinished)
//            }
//            else {
//                CreateAccountNameNavigator(this.activity, this::childNavigatorFinished)
//            }
        }


        // Start the navigator
        navigator?.start()
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {

        onChildFinished?.invoke(this)
    }
}