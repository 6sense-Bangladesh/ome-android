package com.ome.app.ui.dashboard.profile.change_password

import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.AmplifyResultValue
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val amplifyManager: AmplifyManager) :
    BaseViewModel() {

    val validationSuccessFlow= MutableSharedFlow<Boolean>()
    val validationErrorFlow = MutableSharedFlow<Pair<Validation?, String>>()
    val passwordChangedFlow= MutableSharedFlow<AmplifyResultValue>()

    private var oldPassword = ""
    private var newPassword = ""



    fun updatePassword() = launch(dispatcher = ioContext) {
        loadingLiveData.postValue(true)
        val result =
            amplifyManager.updatePassword(oldPassword = oldPassword, newPassword = newPassword)
        passwordChangedFlow.emit(result)
    }

    enum class Validation{
        OLD_PASSWORD,
        NEW_PASSWORD
    }

    fun validatePassword(old: String, new: String) {
        launch {
            if (old.trim().isEmpty())
                validationErrorFlow.emit(Pair(Validation.OLD_PASSWORD, "Must not be empty"))
            else if (new.trim().isEmpty())
                validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, "Must not be empty"))
            else if (new.length <= 8)
                validationErrorFlow.emit(Pair(Validation.OLD_PASSWORD, "Smaller than 9 characters"))
            else if (new.length > 25)
                validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, "Grater than 26 characters"))
            else if (!new.isValidPassword())
                validationErrorFlow.emit(Pair(Validation.NEW_PASSWORD, "Invalid password"))
            else if (old == new)
                validationErrorFlow.emit(Pair(null, "New password must be different"))
            else{
                oldPassword = old
                newPassword = new
                validationSuccessFlow.emit(true)
            }
        }
    }
}

