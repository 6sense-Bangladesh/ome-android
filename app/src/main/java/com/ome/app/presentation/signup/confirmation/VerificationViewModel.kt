package com.ome.app.presentation.signup.confirmation

import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.ome.app.BuildConfig
import com.ome.app.data.local.PrefKeys
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.Constants
import com.ome.app.utils.IO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class VerificationViewModel @Inject constructor(
    val pref: PreferencesProvider,
    private val amplifyManager: AmplifyManager,
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val params by lazy { VerificationFragmentArgs.fromSavedStateHandle(savedStateHandle).params }

    init {
        pref.utils.saveObject(PrefKeys.AUTH_PARAMS, params)
    }
    
//    var firstName = firstName
//    var lastName = ""
//    var phone = ""
//    var email = ""
    var code = ""
//    var currentPassword = ""
//    var isForgotPassword = false

    val signUpConfirmationResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val resendClickedResultLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()
    val codeValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun confirmSignUp() = launch(ioContext) {
        amplifyManager.confirmSignUp(params.email, code)
        signIn(params.email, params.currentPassword)
        fetchUserData()
        createUser()
    }


    private suspend fun createUser()  {
        pref.getUserId()?.let {
            when (val result = userRepository.createUser(
                CreateUserRequest(
                    deviceTokens = listOf(Firebase.messaging.token.await()),
                    email = params.email,
                    firstName = params.firstName,
                    lastName = params.lastName,
                    phone = params.phone,
                    stoveOrientation = -1,
                    uiAppType = "ANDROID",
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
                    stoveRepository.getAllKnobs()
                    userRepository.getUserData()
                    IO { pref.setTimer(Constants.VERIFICATION_KEY, 0) }
                    signUpConfirmationResultLiveData.postValue(true)
                }
            }
        } ?: run {
            signUpConfirmationResultLiveData.postValue(false)
        }
    }

    fun validateConfirmationCode(code: String) {
        if (code.isBlank()) {
            defaultErrorLiveData.postValue("Please make sure to enter confirmation code.")
            loadingLiveData.postValue(false)
            return
        }

        this.code = code.trim()
        codeValidationLiveData.postValue(true)
    }

    private fun saveUserData(userData: UserResponse) = pref.saveUserData(userData)

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
            pref.saveAccessToken(accessToken)

            userAttributes.attributes?.forEach { attr ->
                if (attr.key.keyString == "sub") {
                    pref.saveUserId(attr.value)
                }
            }
        }
    }

    fun resendCode(email: String) = launch(ioContext) {
        val result = amplifyManager.resendSignUpCode(email)
        IO { pref.setTimer(Constants.VERIFICATION_KEY, Constants.TWO_MINUTES_MILLIS) }
        resendClickedResultLiveData.postValue(result)
    }
}
