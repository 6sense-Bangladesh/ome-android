package com.ome.app.presentation.signup.email

import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import com.ome.app.utils.FieldsValidator
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

    fun validateFields(email: String, phone: String = "") {
        val resultEmail = FieldsValidator.validateEmail(email)
        if (resultEmail.first) {
            this.email = email.trim()
        } else {
            defaultErrorLiveData.postValue(resultEmail.second)
            return
        }

        val resultPhone = FieldsValidator.validatePhone(phone)
        if (resultPhone.first) {
            this.phoneNumber = if (phone.trim().isNotEmpty()) {
                "+1$phone"
            } else {
                ""
            }
        } else {
            defaultErrorLiveData.postValue(resultPhone.second)
            return
        }

        emailAndPassValidationLiveData.postValue(true)
    }
}
