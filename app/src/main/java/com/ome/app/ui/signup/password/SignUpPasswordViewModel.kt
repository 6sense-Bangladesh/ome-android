package com.ome.app.ui.signup.password

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.utils.FieldsValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationSuccessLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val signUpResultLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()
    val passwordResetLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()

    var firstName = ""
    var lastName = ""
    var phone = ""
    var code = ""
    var email = ""
    var currentPassword = ""
    var isForgotPassword = false


    fun signUp() = launch(ioContext) {
        val result = amplifyManager.signUp(email, currentPassword, phone)
        signUpResultLiveData.postValue(result)
    }

    fun confirmResetPassword() = launch(ioContext) {
        val result =
            amplifyManager.confirmResetPassword(password = currentPassword, confirmationCode = code)
        passwordResetLiveData.postValue(result)
    }


    fun resendCode() = launch(ioContext) {
        val result = amplifyManager.resendSignUpCode(email)
        signUpResultLiveData.postValue(result)
    }

    fun validatePassword(password: String, confirmPassword: String) {
        val result = FieldsValidator.validatePassword(password, confirmPassword)
        if(result.first){
            currentPassword = password
            validationSuccessLiveData.setNewValue(true)
        } else {
            defaultErrorLiveData.setNewValue(result.second)
        }
    }

}
