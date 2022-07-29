package com.example.inirv.AppLevel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.R

enum class AppLevelChildFlow{
    loginCreateAccount,
    initialSetup,
    setupANewDevice,
    stoveHome
}

class AppLevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_level)

        // Setup the navigator
//        val appNavigator = AppNavigator(this, onChildFinished = this::createChildActivity)
//        appNavigator.start()

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, LaunchFragment.newInstance())
//                .commitNow()
//        }
    }

    fun createChildActivity(navigator: Navigator){

        var childActivity: AppCompatActivity? = null

        if (navigator is AppNavigator){
            val navigatorUserflow = navigator.userflow

//            when(navigatorUserflow){
//                Userflow.createAccount -> {
//                    childActivity = CreateAccountActivity()
//                }
//                Userflow.logginIn -> {
//                    childActivity = LoginActivity()
//                }
                // TODO:: Add the other flows as the activities are implemented
//                Userflow.setupStove -> {
//
//                }
//                Userflow.homeScreen -> {
//
//                }
//                Userflow.setupANewDeviceHome, Userflow.setupANewDeviceHome -> {
//
//                }
//                Userflow.logginOut -> {
//
//                }
//                else -> {
//
//                }
//            }
        }

        val runActivity = childActivity?.let {
            it.startActivity(this.intent)
        }
    }
}