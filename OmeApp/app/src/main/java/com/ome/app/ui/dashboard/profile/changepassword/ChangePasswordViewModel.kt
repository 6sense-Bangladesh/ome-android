package com.ome.app.ui.dashboard.profile.changepassword

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationSuccessLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val passwordChangedLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()

    var oldPassword = ""
    var newPassword = ""



    fun updatePassword() = launch(dispatcher = ioContext) {
        val result =
            amplifyManager.updatePassword(oldPassword = oldPassword, newPassword = newPassword)
        passwordChangedLiveData.postValue(result)
    }


    fun validatePassword(old: String, new: String) {
        if (old.trim().isEmpty()) {
            defaultErrorLiveData.setNewValue("Please make sure to enter an old password")
            return
        } else if (new.trim().isEmpty()) {
            defaultErrorLiveData.setNewValue("Please make sure to enter a new password")
            return
        } else if (new.length <= 8) {
            defaultErrorLiveData.setNewValue("Please make sure that your new password length is 9 characters or more")
            return
        }  else if (new.length > 25) {
            defaultErrorLiveData.setNewValue("Please make sure that your new password length is less than 26 characters")
            return
        } else {

            // Check for at least three out of the four cases: upper case, lower case, number, special
            var hasUpper = false
            var hasLower = false
            var hasNumber = false
            var hasSpecial = false

            for (char in new) {

                if (char.isUpperCase()) {
                    hasUpper = true
                } else if (char.isLowerCase()) {
                    hasLower = true
                } else if (char.isDigit()) {
                    hasNumber = true
                }
            }

            if (containsSpecialCharacter(new)) {
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

        oldPassword = old
        newPassword = old
        validationSuccessLiveData.setNewValue(true)
    }

    private fun containsSpecialCharacter(sequence: String): Boolean {

        val pattern = Regex("^\\$\\*\\.\\[\\]\\{\\}\\(\\)\\?\"!@#%&/\\,<>':;|_~=+-`")

        return pattern.containsMatchIn(sequence)
    }
}

