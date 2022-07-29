package com.example.inirv.Home.Stove

import androidx.fragment.app.FragmentActivity
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.R

class StoveNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) : Navigator{
    override fun start() {

        val coordinator = StoveCoordinator(
            navigator = this
        )
        val viewModel = StoveViewModel()
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.container
            , StoveFragment.newInstance()
        )
            ?.commitNow()

    }

    override fun goToScreen(coordinator: Coordinator) {
//        TODO("Not yet implemented")
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {
        TODO("Not yet implemented")
    }

}