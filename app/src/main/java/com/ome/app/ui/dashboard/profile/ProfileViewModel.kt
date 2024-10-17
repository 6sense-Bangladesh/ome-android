package com.ome.app.ui.dashboard.profile

import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.model.ui.UserProfileItemModel
import com.ome.app.model.ui.toItemModel
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.model.network.request.CreateUserRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val amplifyManager: AmplifyManager,
    val userRepository: UserRepository,
    val preferencesProvider: PreferencesProvider
) : BaseViewModel() {

    val userLiveData: SingleLiveEvent<UserProfileItemModel> = SingleLiveEvent()

    fun signOut(onEnd: () -> Unit) {
        launch(dispatcher = ioContext) {
            amplifyManager.signUserOut()
            preferencesProvider.clearData()
            userRepository.userFlow.emit(null)
            amplifyManager.signOutFlow.emit(true)
            onEnd()
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
        onEnd()
    }
}
