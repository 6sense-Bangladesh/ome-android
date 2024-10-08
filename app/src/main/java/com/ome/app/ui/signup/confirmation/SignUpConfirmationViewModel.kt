package com.ome.app.ui.signup.confirmation

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.model.network.request.CreateUserRequest
import com.ome.app.model.local.User
import com.ome.app.model.base.ResponseWrapper
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

    fun confirmSignUp() = launch(dispatcher = ioContext) {
        amplifyManager.confirmSignUp(email, code)
        signIn(email, currentPassword)
        saveUserData()
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
                    uiAppType = "Android",
                    uiAppVersion = "1.7 (10)",
                    userId = it
                )
            )) {
                is ResponseWrapper.NetworkError -> {
                    loadingLiveData.postValue(false)
                }
                is ResponseWrapper.GenericError -> {
                    result.response?.message?.let { message ->
                        defaultErrorLiveData.postValue(message)
                        signUpConfirmationResultLiveData.postValue(false)
                        loadingLiveData.postValue(false)
                    }
                }
                is ResponseWrapper.Success -> {
                    signUpConfirmationResultLiveData.postValue(true)
                }
                else -> {}
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

    private fun saveUserData() = preferencesProvider.saveUserData(
        User(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phone,
            email = email
        )
    )

    suspend fun signIn(username: String, password: String): AmplifyResultValue =
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

    fun resendCode(email: String) = launch(dispatcher = ioContext) {
        val result = amplifyManager.resendSignUpCode(email)
        resendClickedResultLiveData.postValue(result)
    }
}