package com.ome.app.ui.signup.welcome

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val preferencesProvider: PreferencesProvider,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var firstName = ""
    var lastName = ""
    var phoneNumber = ""
    var currentPassword = ""
    var email = ""

    val fetchUserDataStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun setup() = launch(ioContext) {

    }


}
