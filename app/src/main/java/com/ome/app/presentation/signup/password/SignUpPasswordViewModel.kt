package com.ome.app.presentation.signup.password

import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.isValidPasswordResult
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationSuccessLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val passwordResetLiveData: SingleLiveEvent<AmplifyResultValue> = SingleLiveEvent()
    val validationErrorFlow = MutableSharedFlow<List<Pair<Validation, String>>>()
    val validationSuccessFlow = MutableSharedFlow<AmplifyResultValue>()

    var firstName = ""
    var lastName = ""
    var phone = ""
    var code = ""
    var email = ""
    var currentPassword = ""
    var retypePassword = ""
    var isForgotPassword = false


    fun confirmResetPassword() = launch(ioContext) {
        val result =
            amplifyManager.confirmResetPassword(password = currentPassword, confirmationCode = code)
        passwordResetLiveData.postValue(result)
    }

    fun validateFields(currentPassword: String, retypePassword: String) {
        this.currentPassword = currentPassword
        this.retypePassword = retypePassword

        val validationList = mutableListOf<Pair<Validation, String>>()

        val passValidator = currentPassword.isValidPasswordResult()

        launch(ioContext) {
            if (currentPassword.isBlank()) {
                validationList.add(Pair(Validation.NEW_PASSWORD, DefaultValidation.REQUIRED))
            } else if (passValidator is ResponseWrapper.Error) {
                validationList.add(Pair(Validation.NEW_PASSWORD, passValidator.message))
            }

            if (retypePassword.isBlank()) {
                validationList.add(Pair(Validation.RE_PASSWORD, DefaultValidation.REQUIRED))
            } else if (currentPassword != retypePassword) {
                validationList.add(Pair(Validation.RE_PASSWORD, "Password doesn't match."))
            }
            validationErrorFlow.emit(validationList)
            if (validationList.isEmpty()) {
                validationSuccessFlow.emit(
                    amplifyManager.confirmResetPassword(currentPassword, code)
                )
            }
        }
    }
}
