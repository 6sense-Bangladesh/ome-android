package com.ome.app.ui.signin.forgotpassword

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.utils.Constants
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
        if (!Constants.EMAIL_ADDRESS_PATTERN.matcher(email.trim()).matches()) {
            defaultErrorLiveData.postValue("Please make sure you're using a valid email")
            loadingLiveData.postValue(false)
            return
        }
        this.email = email.trim()

        emailAndPassValidationLiveData.postValue(true)
    }

}
