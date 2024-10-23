package com.ome.app.ui.dashboard.profile.change_password

import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.domain.model.base.DefaultValidation
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.base.Validation
import com.ome.app.domain.model.base.isValidPasswordResult
import com.ome.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationSuccessFlow= MutableSharedFlow<Boolean>()
    val validationErrorFlow = MutableSharedFlow<Pair<Validation, String>>()
    val passwordChangedFlow= MutableSharedFlow<AmplifyResultValue>()

    private var oldPassword = ""
    private var newPassword = ""



    fun updatePassword() = launch(ioContext) {
        loadingLiveData.postValue(true)
        val result =
            amplifyManager.updatePassword(oldPassword = oldPassword, newPassword = newPassword)
        passwordChangedFlow.emit(result)
    }

    fun validatePassword(old: String, new: String) {
        launch {
            val validator = new.isValidPasswordResult()
            when {
                old.trim().isEmpty() -> validationErrorFlow.emit(Pair(Validation.OLD_PASSWORD, DefaultValidation.REQUIRED))
                old.trim().isEmpty() && new.trim().isEmpty() -> validationErrorFlow.emit(Pair(Validation.ALL_FIELDS, DefaultValidation.REQUIRED))
                validator is ResponseWrapper.Error-> validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, validator.message))
                old == new -> validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, "New password must be different."))
                else -> {
                    oldPassword = old
                    newPassword = new
                    validationSuccessFlow.emit(true)
                }
            }
        }
    }
}

