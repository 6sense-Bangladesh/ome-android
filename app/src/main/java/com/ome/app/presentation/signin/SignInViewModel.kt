package com.ome.app.presentation.signin

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.isFalse
import com.ome.app.utils.isValidEmail
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val pref: PreferencesProvider,
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository
) : BaseViewModel() {

    val destinationAfterSignFlow = MutableSharedFlow<Int>()

    fun signIn(username: String, password: String): Pair<Validation, String>?{
        return if (username.isEmpty())
            Validation.EMAIL to DefaultValidation.REQUIRED
        else if (!username.isValidEmail())
            Validation.EMAIL to DefaultValidation.INVALID_EMAIL
        else if (password.isEmpty())
            Validation.OLD_PASSWORD to DefaultValidation.REQUIRED
        else {
            launch(ioContext) {
                amplifyManager.signUserIn(username.trim(), password)
                fetchUserData()
            }
            null
        }
    }

    private suspend fun fetchUserData(){
        val userAttributes =
            withContext(Dispatchers.Default) { amplifyManager.fetchUserAttributes() }
        val authSession =
            withContext(Dispatchers.Default) { amplifyManager.fetchAuthSession() }

        if (authSession.session is AWSCognitoAuthSession) {
            val accessToken =
                (authSession.session as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
            logi("accessToken: $accessToken")
            pref.saveAccessToken(accessToken)

            userAttributes.attributes?.forEach { attr ->
                if (attr.key.keyString == "sub") {
                    logi("userId: ${attr.value}")
                    pref.saveUserId(attr.value)
                }
            }
            when (val result = userRepository.getUserData()) {
                is ResponseWrapper.Error -> {
                    if (result.message.startsWith("Not found")) {
                        amplifyManager.deleteUser()
                        pref.clearData()
                        error("User not found.")
                    }else error(result.message)
                }
                is ResponseWrapper.Success -> {
                    userRepository.updateUser(CreateUserRequest(
                        deviceTokens = result.value.deviceTokens.toMutableList().apply {
                            add(Firebase.messaging.token.await())
                        }.distinct(),
                        uiAppType = "ANDROID",
                        uiAppVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    ))
                    loadingLiveData.postValue(false)
                    stoveRepository.getAllKnobs()
                    pref.saveUserData(result.value)
                    if (result.value.stoveSetupComplete.isFalse())
                        destinationAfterSignFlow.emit(R.id.action_signInFragment_to_welcomeFragment)
                    else
                        destinationAfterSignFlow.emit(R.id.action_signInFragment_to_dashboardFragment)
                }
            }
        }
    }
}
