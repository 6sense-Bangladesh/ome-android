package com.ome.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.ome.Ome.R
import com.ome.app.base.BaseViewModel
import com.ome.app.data.local.PreferencesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(val preferencesProvider: PreferencesProvider) :
    BaseViewModel() {


    lateinit var isStartDestination: LiveData<Boolean>
    lateinit var currentBottomNavController: LiveData<NavController>
    val currentDestination = MutableStateFlow<NavDestination?>(null)

    fun connectBottomNavController(navControllerFlow: StateFlow<NavController>) {
        currentBottomNavController = navControllerFlow.asLiveData()

        launch {
            navControllerFlow.collect {
                if (it.graph.id == R.id.myStoveNavigation) {
//                    val inflater = it.navInflater
//
//                    val graph = inflater.inflate(R.navigation.my_stove_navigation)
//                    graph.setStartDestination(R.id.myStoveFragment)
//                    it.setGraph(graph,null)
                }
            }
        }

        isStartDestination =
            combine(currentDestination, navControllerFlow) { navDestination, navController ->

                navController.graph.startDestinationId == navDestination?.id

            }.asLiveData()
    }

    fun isStoveInfoExist(): Boolean = preferencesProvider.getUserData() != null

}
