package com.ome.app.presentation.dashboard

import androidx.navigation.NavController
import com.ome.app.R
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    val preferencesProvider: PreferencesProvider,
    val amplifyManager: AmplifyManager,
    val userRepository: UserRepository
) : BaseViewModel() {

//    lateinit var isStartDestination: LiveData<Boolean>
//    lateinit var currentBottomNavController: LiveData<NavController>
//    val bottomBarVisible: LiveData<Boolean> = MutableLiveData(true)
    val stoveExistLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val signOutLiveData = SingleLiveEvent<Any>()

    init {
//        launch {
//            currentDestination.collect {
//                bottomBarVisible.mutable().value = when (it?.id) {
//                    R.id.settingsFragment,
//                    R.id.myStoveFragment,
//                    R.id.membersFragment,
//                    R.id.profileFragment,
//                    R.id.welcomeFragment -> true
//                    else -> false
//                }
//            }
//        }
//        launch(ioContext) {
//            userRepository.userFlow.collect { user ->
//                user?.let {
//                    stoveExistLiveData.postValue(it.stoveId != null)
//                }
//            }
//        }
    }

    fun connectBottomNavController(navControllerFlow: StateFlow<NavController>) {
//        currentBottomNavController = navControllerFlow.asLiveData()

        launch(ioContext) {
            amplifyManager.signOutFlow.collect {
                if (it) {
                    signOutLiveData.postValue(Unit)
                    amplifyManager.signOutFlow.emit(false)
                }
            }
        }

        launch {
            navControllerFlow.collect {
                if (it.graph.id == R.id.myStoveSetupNavGraph) {
//                    val inflater = it.navInflater
//
//                    val graph = inflater.inflate(R.navigation.my_stove_navigation)
//                    graph.setStartDestination(R.id.myStoveFragment)
//                    it.setGraph(graph,null)
                }
            }
        }

//        isStartDestination =
//            combine(currentDestination, navControllerFlow) { navDestination, navController ->
//                navController.graph.startDestinationId == navDestination?.id
//
//            }.asLiveData()
    }

    fun isStoveInfoExist(): Boolean = userRepository.userFlow.value?.stoveId != null

}
