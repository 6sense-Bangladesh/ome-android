package com.ome.app.presentation.signin.forgot_password

import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.*
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationErrorFlow = MutableSharedFlow<List<Pair<Validation, String>>>()
    val validationSuccessFlow = MutableSharedFlow<AmplifyResultValue>()

    var firstName = ""
    var lastName = ""
    var phone = ""
    var code = ""
    var email = ""
    private var currentPassword = ""
    private var retypePassword = ""


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
