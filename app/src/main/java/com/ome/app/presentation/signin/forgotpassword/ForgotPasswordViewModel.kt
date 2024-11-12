package com.ome.app.presentation.signin.forgotpassword

import android.util.Patterns
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(val amplifyManager: AmplifyManager): BaseViewModel() {

    val forgotPasswordSuccess: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()
    val emailAndPassValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    var email = ""

    fun forgotPassword()  = launch(ioContext) {
        val result = amplifyManager.resetPassword(email)
        forgotPasswordSuccess.postValue(result)
    }

    fun validateEmail(email: String) {
        if (email.trim().isEmpty()) {
            defaultErrorLiveData.postValue("Please make sure to enter an email")
            loadingLiveData.postValue(false)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            defaultErrorLiveData.postValue("Please make sure you're using a valid email")
            loadingLiveData.postValue(false)
            return
        }
        this.email = email.trim()

        emailAndPassValidationLiveData.postValue(true)
    }

}
