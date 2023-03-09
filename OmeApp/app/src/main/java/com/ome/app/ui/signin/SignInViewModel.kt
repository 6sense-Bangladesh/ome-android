package com.ome.app.ui.signin

import android.os.Bundle
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.Ome.R
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    val preferencesProvider: PreferencesProvider,
    val userRepository: UserRepository
) : BaseViewModel() {

    val signInStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val deleteStatus: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val destinationAfterSignInLiveData = SingleLiveEvent<Pair<Int, Bundle?>>()

    override var defaultErrorHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            loadingLiveData.postValue(false)
            if (throwable is AuthException) {
                sendError(Throwable("Incorrect username or password"))
            } else {
                sendError(throwable)
            }

        }

    fun deleteUser() = launch(dispatcher = ioContext) {
        val result = amplifyManager.deleteUser()
        if (result.wasCallSuccessful) {
            deleteStatus.postValue(true)
        } else {
            defaultErrorLiveData.postValue(result.authException?.message)
            deleteStatus.postValue(false)
        }
    }

    fun signIn(username: String, password: String) = launch(dispatcher = ioContext) {
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

    fun fetchUserData() = launch(dispatcher = ioContext) {
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
                is ResponseWrapper.NetworkError -> {
                    loadingLiveData.postValue(false)
                }
                is ResponseWrapper.GenericError -> {
                    result.response?.message?.let { message ->
                        loadingLiveData.postValue(false)
                        if (message.contains("Not found")) {

                        } else {
                            loadingLiveData.postValue(false)
                        }
                    }
                }
                is ResponseWrapper.Success -> {
                    loadingLiveData.postValue(false)
                    destinationAfterSignInLiveData.postValue(R.id.action_signInFragment_to_dashboardFragment to null)
                }
                else -> {}
            }
        }
    }
}
