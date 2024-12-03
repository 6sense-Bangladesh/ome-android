package com.ome.app.presentation.dashboard.profile

import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val userRepository: UserRepository,
    private val pref: PreferencesProvider
) : BaseViewModel() {
    
    val validationErrorFlow = MutableSharedFlow<Pair<Validation?, String>>()

    fun updateUserName(firstName: String, lastName: String)= launch(ioContext) {
        when {
            firstName.isEmpty() && lastName.isEmpty() -> validationErrorFlow.emit(Pair(Validation.ALL_FIELDS, DefaultValidation.REQUIRED))
            firstName.isEmpty() -> validationErrorFlow.emit(Pair(Validation.FIRST_NAME, DefaultValidation.REQUIRED))
            lastName.isEmpty() -> validationErrorFlow.emit(Pair(Validation.LAST_NAME, DefaultValidation.REQUIRED))
            else -> {
                userRepository.userFlow.value?.let {
                    if (firstName == it.firstName && lastName == it.lastName){
                        return@launch
                    }
                }
                userRepository.updateUser(CreateUserRequest(firstName = firstName, lastName = lastName))
                userRepository.getUserData()
            }
        }
    }

    fun deleteUser(onEnd: () -> Unit) = launch(ioContext) {
        userRepository.deleteUser()
        amplifyManager.deleteUser()
        pref.clearData()
        amplifyManager.signOutFlow.emit(true)
        withContext(mainContext) {
            onEnd()
        }
    }
}
