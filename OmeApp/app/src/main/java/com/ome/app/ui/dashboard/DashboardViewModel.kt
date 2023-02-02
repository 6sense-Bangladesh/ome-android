package com.ome.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ome.Ome.R
import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.utils.mutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    val preferencesProvider: PreferencesProvider,
    val amplifyManager: AmplifyManager,
    val userRepository: UserRepository
) : BaseViewModel() {

    lateinit var isStartDestination: LiveData<Boolean>
    lateinit var currentBottomNavController: LiveData<NavController>
    val bottomBarVisible: LiveData<Boolean> = MutableLiveData(true)
    val stoveExistLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val signOutLiveData = SingleLiveEvent<Any>()

    init {
        launch {
            currentDestination.collect {
                bottomBarVisible.mutable().value = when (it?.id) {
                    R.id.settingsFragment,
                    R.id.myStoveFragment,
                    R.id.membersFragment,
                    R.id.profileFragment,
                    R.id.welcomeFragment -> true
                    else -> false
                }
            }
        }
        launch(dispatcher = ioContext) {
            userRepository.userFlow.collect { user ->
                user?.let {
                    stoveExistLiveData.postValue(it.stoveId != null)
                }
            }
        }
    }

    fun connectBottomNavController(navControllerFlow: StateFlow<NavController>) {
        currentBottomNavController = navControllerFlow.asLiveData()

        launch(dispatcher = ioContext) {
            amplifyManager.signOutFlow.collect {
                if (it) {
                    signOutLiveData.postValue(Unit)
                    amplifyManager.signOutFlow.emit(false)
                }
            }
        }

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

    fun isStoveInfoExist(): Boolean = userRepository.userFlow.value?.stoveId != null

}
