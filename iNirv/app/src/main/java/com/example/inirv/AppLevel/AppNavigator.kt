package com.example.inirv.AppLevel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.inirv.AppLevel.ui.launch.LaunchFragment
import com.example.inirv.AppLevel.ui.launch.LaunchViewModel
import com.example.inirv.Enums.Userflow
import com.example.inirv.Home.HomeTab.StoveTabNavigator
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.LoginAndCreateAccount.Welcome.WelcomeNavigator
import com.example.inirv.R
import com.example.inirv.SetupANewDevice.KnobInstallationOne.KnobInstallationOneNavigator
import com.example.inirv.SetupStove.SetupBrand.SetupStoveBrandNavigator

enum class AppNavigatorScreen{
    welcome,
    home,
    stoveBrand,
    knobInstallationOne
}

class AppNavigator(override var activity: FragmentActivity?,
                   override var onChildFinished: ((Navigator) -> Unit)?,
                   val _userflow: Userflow = Userflow.normal
) : Navigator {

    var userflow: Userflow  // Userflow we're currently in
        private set
    var navController: NavController? = null
        private set

    init {
        userflow = _userflow
    }

    override fun start() {

        val coordinator = AppCoordinator(
            navigator = this
        )

        val viewModel = LaunchViewModel(
            onFinished= coordinator::coordinatorInteractorFinished,
            _sharedPreferences = activity?.getSharedPreferences("omePreferences", Context.MODE_PRIVATE))

//        val fragment = LaunchFragment.newInstance(viewModel)

//        navController = fragment.findNavController()

//        val action = LaunchFragmentDirections.actionLaunchFragmentToLoginNavGraph()
//        activity?.findNavController(R.id.welcomeFragment)
//        fragment.findNavController().navigate(R.id.launchFragment)

        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.container,
            LaunchFragment.newInstance()
        )
            ?.commitNow()

        // Start the view model
//        viewModel.setup()
    }

    override fun goToScreen(coordinator: Coordinator) {

        // Check for the correct values based on the coordinator
        var navigator: Navigator? = null

        // Course of action if the coordinator is AppCoordinator
        if (coordinator is AppCoordinator){

            when(coordinator.goToScreen){
                AppNavigatorScreen.welcome -> {

                    navigator = WelcomeNavigator(this.activity, this::childNavigatorFinished)
                }
                AppNavigatorScreen.home -> {
                    navigator = StoveTabNavigator(this.activity, this::childNavigatorFinished)
//                    navigator = StoveNavigator(this.activity, this::childNavigatorFinished)
                }
                AppNavigatorScreen.stoveBrand -> {

                    navigator = SetupStoveBrandNavigator(this.activity, this::childNavigatorFinished)
                }
                AppNavigatorScreen.knobInstallationOne -> {

                    navigator = KnobInstallationOneNavigator(this.activity, this::childNavigatorFinished)
                }
            }
        }

        // If there is a navigator
        navigator?.start()
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {

    }


}