package com.ome.app.presentation.signin

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.app.R
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.isFalse
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val preferencesProvider: PreferencesProvider,
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository
) : BaseViewModel() {

    val signInStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val deleteStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val destinationAfterSignInLiveData = SingleLiveEvent<Int>()

    fun deleteUser() = launch(ioContext) {
        val result = amplifyManager.deleteUser()
        if (result.isSuccessful) {
            deleteStatus.postValue(true)
        } else {
            defaultErrorLiveData.postValue(result.authException?.message)
            deleteStatus.postValue(false)
        }
    }

    fun signIn(username: String, password: String) = launch(ioContext) {
        if (username.isEmpty()) {
            defaultErrorLiveData.postValue("Please make sure to enter an email")
            signInStatus.postValue(false)
            return@launch
        }
        if (password.isEmpty()) {
            defaultErrorLiveData.postValue("Please make sure to enter an password")
            signInStatus.postValue(false)
            return@launch
        }
        amplifyManager.signUserIn(username.trim(), password)
        signInStatus.postValue(true)
    }

    fun fetchUserData() = launch(ioContext) {
        val userAttributes =
            withContext(Dispatchers.Default) { amplifyManager.fetchUserAttributes() }
        val authSession =
            withContext(Dispatchers.Default) { amplifyManager.fetchAuthSession() }

        if (authSession.session is AWSCognitoAuthSession) {
            val accessToken =
                (authSession.session as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
            logi("accessToken: $accessToken")
            preferencesProvider.saveAccessToken(accessToken)

            userAttributes.attributes?.forEach { attr ->
                if (attr.key.keyString == "sub") {
                    logi("userId: ${attr.value}")
                    preferencesProvider.saveUserId(attr.value)
                }
            }
            when (val result = userRepository.getUserData()) {
                is ResponseWrapper.Error -> loadingLiveData.postValue(false)
                is ResponseWrapper.Success -> {
                    loadingLiveData.postValue(false)
                    stoveRepository.getAllKnobs()
                    if (result.value.stoveSetupComplete.isFalse()){
                        destinationAfterSignInLiveData.postValue(R.id.action_signInFragment_to_welcomeFragment)
                        return@launch
                    }
                    else
                        destinationAfterSignInLiveData.postValue(R.id.action_signInFragment_to_dashboardFragment)
                }
            }
        }
    }
}
