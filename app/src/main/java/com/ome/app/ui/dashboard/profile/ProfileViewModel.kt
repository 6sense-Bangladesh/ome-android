package com.ome.app.ui.dashboard.profile

import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.repo.UserRepository
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.ui.UserProfileItemModel
import com.ome.app.domain.model.ui.toItemModel
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val userRepository: UserRepository,
    private val preferencesProvider: PreferencesProvider
) : BaseViewModel() {

    val userLiveData: SingleLiveEvent<UserProfileItemModel> = SingleLiveEvent()

    fun signOut(onEnd: () -> Unit) {
        launch(dispatcher = ioContext) {
            amplifyManager.signUserOut()
            preferencesProvider.clearData()
            userRepository.userFlow.emit(null)
            amplifyManager.signOutFlow.emit(true)
            withContext(mainContext) {
                onEnd()
            }
        }
    }

    fun initUserDataSubscription() = launch(dispatcher = ioContext) {
        userRepository.userFlow.collect { user ->
            user?.let {
                userLiveData.postValue(it.toItemModel())
            }
        }
    }

    fun updateFirstName(firstName: String) = launch(dispatcher = ioContext) {
        if (firstName.isEmpty()) {
            return@launch
        }
        userRepository.userFlow.value?.let {
            if (firstName == it.firstName) {
                return@launch
            }
        }
        userRepository.updateUser(CreateUserRequest(firstName = firstName))
        userRepository.getUserData()
    }

    fun updateLastName(lastName: String) = launch(dispatcher = ioContext) {
        if (lastName.isEmpty()) {
            return@launch
        }
        userRepository.userFlow.value?.let {
            if (lastName == it.lastName) {
                return@launch
            }
        }
        userRepository.updateUser(CreateUserRequest(lastName = lastName))
        userRepository.getUserData()
    }


    fun deleteUser(onEnd: () -> Unit) = launch(dispatcher = ioContext) {
        userRepository.deleteUser()
        amplifyManager.deleteUser()
        preferencesProvider.clearData()
        amplifyManager.signOutFlow.emit(true)
        withContext(mainContext) {
            onEnd()
        }
    }
}
