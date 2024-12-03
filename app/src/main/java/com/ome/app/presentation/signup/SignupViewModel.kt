package com.ome.app.presentation.signup

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.isValidPasswordResult
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    val pref: PreferencesProvider,
    private val amplifyManager: AmplifyManager
) : BaseViewModel() {

    val validationErrorFlow = MutableSharedFlow<List<Pair<Validation, String>>>()
    val validationSuccessFlow = MutableSharedFlow<AmplifyResultValue>()

    var firstName = ""
    var lastName = ""
    var email = ""
    var phone = ""
    var password = ""

    fun validateFields(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        val validationList = mutableListOf<Pair<Validation, String>>()

        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.phone = phone
        this.password = password

        val passValidator = password.isValidPasswordResult()

        launch(ioContext) {
            if (firstName.isBlank()) {
                validationList.add(Pair(Validation.FIRST_NAME, DefaultValidation.REQUIRED))
            }
            if (lastName.isBlank()) {
                validationList.add(Pair(Validation.LAST_NAME, DefaultValidation.REQUIRED))
            }
            if (email.isBlank()) {
                validationList.add(Pair(Validation.EMAIL, DefaultValidation.REQUIRED))
            } else if (!email.isValidEmail()) {
                validationList.add(Pair(Validation.EMAIL, DefaultValidation.INVALID_EMAIL))
            }
            if (phone.isNotBlank() && !PhoneNumberUtil.getInstance()
                    .isValidNumber(PhoneNumberUtil.getInstance().parse(phone, "US"))
            ) {
                validationList.add(Pair(Validation.PHONE, DefaultValidation.INVALID_PHONE))
            }
            if (password.isBlank()) {
                validationList.add(Pair(Validation.NEW_PASSWORD, DefaultValidation.REQUIRED))
            } else if (passValidator is ResponseWrapper.Error) {
                validationList.add(Pair(Validation.NEW_PASSWORD, passValidator.message))
            }
            if (confirmPassword.isBlank()) {
                validationList.add(Pair(Validation.RE_PASSWORD, DefaultValidation.REQUIRED))
            } else if (password != confirmPassword) {
                validationList.add(Pair(Validation.RE_PASSWORD, "Password doesn't match."))
            }
            validationErrorFlow.emit(validationList)
            if (validationList.isEmpty()) {
                validationSuccessFlow.emit(
                    amplifyManager.signUp(email, password, phone)
                )
            }
        }
    }
}