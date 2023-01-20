package com.ome.app.ui.signup.email

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpEmailAndPhoneViewModel @Inject constructor() :
    BaseViewModel() {
    var firstName = ""
    var lastName = ""
    var phoneNumber = ""
    var email = ""

    val emailAndPassValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun validateEmailAndPassword(email: String, phone: String = "") {
        // Check that the email isnt empty
        if (email.trim().isEmpty()) {
            defaultErrorLiveData.postValue("Please make sure to enter an email")
            return
        }
        if (!isValidEmailString(email)) {
            defaultErrorLiveData.postValue("Please make sure you're using a valid email")
            return
        }

        if (phone.isNotEmpty()) {
            val phoneUtil = PhoneNumberUtil.getInstance()
            try {
                if (!phoneUtil.isValidNumber(phoneUtil.parse(phone, "US"))
                ) {
                    defaultErrorLiveData.postValue("Only U.S. phone numbers are supported at this time.")
                    return
                }
            } catch (e: NumberParseException) {
                defaultErrorLiveData.postValue("NumberParseException was thrown: $e")
                return
            }
        }

        this.phoneNumber = if (phone.trim().isNotEmpty()) {
            "+1$phone"
        } else {
            ""
        }
        this.email = email.trim()

        emailAndPassValidationLiveData.postValue(true)
    }

    private fun isValidEmailString(email: String): Boolean {
        return Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }
}
