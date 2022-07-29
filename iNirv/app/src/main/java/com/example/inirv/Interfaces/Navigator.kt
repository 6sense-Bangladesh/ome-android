package com.example.inirv.Interfaces

import androidx.fragment.app.FragmentActivity

//  Essentially like the navigation controller from iOS, you just tell it to go to whichever screens
interface Navigator {

    var activity: FragmentActivity?
    var onChildFinished: ((Navigator) -> Unit)?

    fun start()
    fun goToScreen(coordinator: Coordinator)
    fun childNavigatorFinished(childNavigator: Navigator)
}