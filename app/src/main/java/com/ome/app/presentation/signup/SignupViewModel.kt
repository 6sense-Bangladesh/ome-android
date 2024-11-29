package com.ome.app.presentation.signup

import com.google.i18n.phonenumbers.PhoneNumberUtil
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
class SignupViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationErrorFlow = MutableSharedFlow<Pair<Validation, String>>()
    val validationSuccessFlow = MutableSharedFlow<AmplifyResultValue>()

    var firstName = ""
    var lastName = ""
    var email = ""
    var phone = ""

    fun validateFields(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.phone = phone

        val passValidator = password.isValidPasswordResult()

        launch(ioContext) {
            when {
                firstName.isBlank() -> {
                    validationErrorFlow.emit(
                        Pair(Validation.FIRST_NAME, DefaultValidation.REQUIRED)
                    )
                }

                lastName.isBlank() -> {
                    validationErrorFlow.emit(Pair(Validation.LAST_NAME, DefaultValidation.REQUIRED))
                }

                email.isBlank() -> {
                    validationErrorFlow.emit(Pair(Validation.EMAIL, DefaultValidation.REQUIRED))
                }

                !email.isValidEmail() -> {
                    validationErrorFlow.emit(
                        Pair(
                            Validation.EMAIL,
                            DefaultValidation.INVALID_EMAIL
                        )
                    )
                }

                phone.isNotBlank() && !PhoneNumberUtil.getInstance()
                    .isValidNumber(PhoneNumberUtil.getInstance().parse(phone, "US")) -> {
                    validationErrorFlow.emit(
                        Pair(
                            Validation.PHONE,
                            DefaultValidation.INVALID_PHONE
                        )
                    )
                }

                password.isBlank() -> {
                    validationErrorFlow.emit(
                        Pair(
                            Validation.NEW_PASSWORD,
                            DefaultValidation.REQUIRED
                        )
                    )
                }

                passValidator is ResponseWrapper.Error -> {
                    validationErrorFlow.emit(Pair(Validation.OLD_PASSWORD, passValidator.message))
                }

//                confirmPassValidator is ResponseWrapper.Error -> {
//                    validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, confirmPassValidator.message))
//                }

                confirmPassword.isBlank() -> {
                    validationErrorFlow.emit(
                        Pair(
                            Validation.NEW_PASSWORD,
                            DefaultValidation.REQUIRED
                        )
                    )
                }

                password != confirmPassword -> {
                    validationErrorFlow.emit(
                        Pair(
                            Validation.NEW_PASSWORD,
                            "Passwords don't match."
                        )
                    )
                }

                else -> {
                    validationSuccessFlow.emit(
                        amplifyManager.signUp(email, password, phone)
                    )
                }
            }
        }
    }
}