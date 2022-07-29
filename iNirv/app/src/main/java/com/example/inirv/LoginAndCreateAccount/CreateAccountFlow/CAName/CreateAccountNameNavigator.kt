package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName

import androidx.fragment.app.FragmentActivity
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator

class CreateAccountNameNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) : Navigator {

    override fun start() {
//        val coordinator = CreateAccountNameCoordinator(
//            navigator = this
//        )
//        val viewModel = CreateAccountNameViewModel(
//            onFinished= coordinator::coordinatorInteractorFinished)
//        activity?.supportFragmentManager?.beginTransaction()?.replace(
//            R.id.container
//            , CreateAccountNameFragment.newInstance(viewModel)
//        )
//            ?.commitNow()
    }

    override fun goToScreen(coordinator: Coordinator) {
        TODO("Not yet implemented")
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {
        TODO("Not yet implemented")
    }
}