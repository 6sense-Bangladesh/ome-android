package com.example.inirv.Home.Settings.Settings

import androidx.fragment.app.FragmentActivity
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator

class SettingsNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) :Navigator {
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun goToScreen(coordinator: Coordinator) {
        TODO("Not yet implemented")
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {
        TODO("Not yet implemented")
    }
}