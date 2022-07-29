package com.example.inirv.Home.HomeTab

import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.inirv.Home.Profile.ProfileNavigator
import com.example.inirv.Home.Settings.Settings.SettingsNavigator
import com.example.inirv.Home.Stove.StoveNavigator
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.R
import com.google.android.material.bottomnavigation.BottomNavigationView

enum class StoveTabBarTabs{
    settings,
    myStove,
    profile
}

class StoveTabNavigator(
    override var activity: FragmentActivity?,
    override var onChildFinished: ((Navigator) -> Unit)?
) : Navigator {

    var childNavigators: List<Navigator> = listOf()
    override fun start() {

        // Create child navigators
//        setupChildNavigator(StoveTabBarTabs.settings)
//        setupChildNavigator(StoveTabBarTabs.myStove)
//        setupChildNavigator(StoveTabBarTabs.profile)

        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.container
            , StoveTabFragment.newInstance()
        )?.commitNow()

        var navController = activity?.findNavController(R.id.stove_tab_nav_host_fragment)
        val bottomNavigationView: BottomNavigationView = activity?.findViewById(R.id.stove_bottom_navigation_bar) as BottomNavigationView

        navController?.let {
            bottomNavigationView.setupWithNavController(navController)
        }


    }

    private fun setupChildNavigator(child: StoveTabBarTabs){

        var childNavigator = when (child){
            StoveTabBarTabs.settings -> {
                SettingsNavigator(this.activity, this::childNavigatorFinished)
            }
            StoveTabBarTabs.myStove -> {
                StoveNavigator(this.activity, this::childNavigatorFinished)
            }
            StoveTabBarTabs.profile -> {
                ProfileNavigator(this.activity, this::childNavigatorFinished)
            }
        }

        val tempList: MutableList<Navigator> = childNavigators.toMutableList()
        tempList.add(childNavigator)

        childNavigators = tempList.toList()
    }

    override fun goToScreen(coordinator: Coordinator) {
//        TODO("Not yet implemented")
    }

    override fun childNavigatorFinished(childNavigator: Navigator) {
//        TODO("Not yet implemented")
    }
}