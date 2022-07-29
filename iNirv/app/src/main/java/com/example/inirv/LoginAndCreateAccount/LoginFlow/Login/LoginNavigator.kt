package com.example.inirv.LoginAndCreateAccount.LoginFlow.Login

import androidx.fragment.app.FragmentActivity
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAConfirm.CAConfirmNavigator
import com.example.inirv.LoginAndCreateAccount.LoginFlow.ForgotPassword.ForgotPasswordNavigator

enum class LoginGoToScreens{
    forgotPassword,
    caConfirm,
    parentNavigator
}

class LoginNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) : Navigator {
    override fun start() {

//        val coordinator = LoginCoordinator(
//            navigator = this
//        )
//        val viewModel = LoginViewModel(
//            onFinished= coordinator::coordinatorInteractorFinished)
//        activity?.supportFragmentManager?.beginTransaction()?.replace(
//            R.id.container
//            , LoginFragment.newInstance(viewModel, viewModel)
//        )
//            ?.commitNow()
    }

    override fun goToScreen(coordinator: Coordinator) {
        var navigator: Navigator? = null

        if (coordinator is LoginCoordinator){

            when(coordinator.screen){
                LoginGoToScreens.forgotPassword -> {
                    navigator = ForgotPasswordNavigator(this.activity, this::childNavigatorFinished)
                }
                LoginGoToScreens.caConfirm -> {
                    navigator = CAConfirmNavigator(this.activity, this::childNavigatorFinished)
                }
                LoginGoToScreens.parentNavigator -> {
                    onChildFinished?.invoke(this)
                    return
                }
            }
        }


        // Start the navigator
        navigator?.start()
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {

        onChildFinished?.invoke(this)
    }
}