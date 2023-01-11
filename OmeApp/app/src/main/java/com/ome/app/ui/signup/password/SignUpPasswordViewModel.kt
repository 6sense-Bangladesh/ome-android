package com.ome.app.ui.signup.password

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
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


    fun signUp() = launch(dispatcher = ioContext) {
        val result = amplifyManager.signUp(email, currentPassword, phone)
        signUpResultLiveData.postValue(result)
    }

    fun confirmResetPassword() = launch(dispatcher = ioContext) {
        val result =
            amplifyManager.confirmResetPassword(password = currentPassword, confirmationCode = code)
        passwordResetLiveData.postValue(result)
    }


    fun resendCode() = launch(dispatcher = ioContext) {
        val result = amplifyManager.resendSignUpCode(email)
        signUpResultLiveData.postValue(result)
    }

    fun validatePassword(password: String, confirmPassword: String) {
        if (password.trim().isEmpty()) {
            defaultErrorLiveData.setNewValue("Please make sure to enter a password")
            return
        } else if (confirmPassword.trim().isEmpty()) {
            defaultErrorLiveData.setNewValue("Please make sure to enter a confirmation password")
            return
        } else if (password.length <= 8) {
            defaultErrorLiveData.setNewValue("Please make sure that your password length is 9 characters or more")
            return
        } else if (confirmPassword.length <= 8) {
            defaultErrorLiveData.setNewValue("Please make sure that your confirmation password length is 9 characters or more")
            return
        } else if (password.length > 25) {
            defaultErrorLiveData.setNewValue("Please make sure that your password length is less than 26 characters")
            return
        } else if (confirmPassword.length > 25) {
            defaultErrorLiveData.setNewValue("Please make sure that your confirmation password length is less than 26 characters")
            return
        } else if (password != confirmPassword) {
            defaultErrorLiveData.setNewValue("Please make sure that your passwords match")
            return
        } else {

            // Check for at least three out of the four cases: upper case, lower case, number, special
            var hasUpper = false
            var hasLower = false
            var hasNumber = false
            var hasSpecial = false

            for (char in password) {

                if (char.isUpperCase()) {
                    hasUpper = true
                } else if (char.isLowerCase()) {
                    hasLower = true
                } else if (char.isDigit()) {
                    hasNumber = true
                }
            }

            if (containsSpecialCharacter(password)) {
                hasSpecial = true
            }

            var qualificationsCounter = 0

            if (hasUpper) {
                qualificationsCounter += 1
            }
            if (hasLower) {
                qualificationsCounter += 1
            }
            if (hasNumber) {
                qualificationsCounter += 1
            }
            if (hasSpecial) {
                qualificationsCounter += 1
            }

            if (qualificationsCounter < 3) {
                defaultErrorLiveData.setNewValue("Please make sure to include at least three of the following types: upper case, lower case, number and/or special character")
                return
            }
        }

        currentPassword = password
        validationSuccessLiveData.setNewValue(true)
    }

    private fun containsSpecialCharacter(sequence: String): Boolean {

        val pattern = Regex("^\\$\\*\\.\\[\\]\\{\\}\\(\\)\\?\"!@#%&/\\,<>':;|_~=+-`")

        return pattern.containsMatchIn(sequence)
    }
}
