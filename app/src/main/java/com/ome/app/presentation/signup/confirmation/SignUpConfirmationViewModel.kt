package com.ome.app.presentation.signup.confirmation

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.app.BuildConfig
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SignUpConfirmationViewModel @Inject constructor(
    val amplifyManager: AmplifyManager,
    val preferencesProvider: PreferencesProvider,
    val userRepository: UserRepository
) :
    BaseViewModel() {

    var firstName = ""
    var lastName = ""
    var phone = ""
    var email = ""
    var code = ""
    var currentPassword = ""
    var isForgotPassword = false

    val signUpConfirmationResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val resendClickedResultLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()
    val codeValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun confirmSignUp() = launch(ioContext) {
        amplifyManager.confirmSignUp(email, code)
        signIn(email, currentPassword)
        fetchUserData()
        createUser()
    }


    private suspend fun createUser()  {
        preferencesProvider.getUserId()?.let {
            when (val result = userRepository.createUser(
                CreateUserRequest(
                    deviceTokens = listOf(),
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    stoveOrientation = -1,
                    uiAppType = "Android",
                    uiAppVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    userId = it
                )
            )) {
                is ResponseWrapper.Error -> {
                    defaultErrorLiveData.postValue(result.message)
                    signUpConfirmationResultLiveData.postValue(false)
                    loadingLiveData.postValue(false)
                }
                is ResponseWrapper.Success -> {
                    saveUserData(result.value)
                    signUpConfirmationResultLiveData.postValue(true)
                }
            }
        } ?: run {
            signUpConfirmationResultLiveData.postValue(false)
        }
    }

    fun validateConfirmationCode(code: String) {
        if (code.trim().isEmpty()) {
            defaultErrorLiveData.postValue("Please make sure to enter confirmation code.")
            loadingLiveData.postValue(false)
            return
        }

        this.code = code.trim()
        codeValidationLiveData.postValue(true)
    }

    private fun saveUserData(userData: UserResponse) = preferencesProvider.saveUserData(userData)

    private suspend fun signIn(username: String, password: String): AmplifyResultValue =
        amplifyManager.signUserIn(username.trim(), password)


    private suspend fun fetchUserData()  {
        val userAttributes =
            withContext(Dispatchers.Default) { amplifyManager.fetchUserAttributes() }
        val authSession =
            withContext(Dispatchers.Default) { amplifyManager.fetchAuthSession() }

        if (authSession.session is AWSCognitoAuthSession) {
            val accessToken =
                (authSession.session as AWSCognitoAuthSession).userPoolTokens.value?.accessToken
            preferencesProvider.saveAccessToken(accessToken)

            userAttributes.attributes?.forEach { attr ->
                if (attr.key.keyString == "sub") {
                    preferencesProvider.saveUserId(attr.value)
                }
            }
        }
    }

    fun resendCode(email: String) = launch(ioContext) {
        val result = amplifyManager.resendSignUpCode(email)
        resendClickedResultLiveData.postValue(result)
    }
}