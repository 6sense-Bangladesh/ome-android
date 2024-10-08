package com.ome.app.ui.signup.name

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.utils.FieldsValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpNameViewModel @Inject constructor() : BaseViewModel() {

    val firstAndLastNameValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun validateFirstAndLastName(firstName: String, lastName: String) {
        val result = FieldsValidator.validateFirstAndLastName(firstName, lastName)
        if (result.first) {
            firstAndLastNameValidationLiveData.postValue(true)
        } else {
            defaultErrorLiveData.postValue(result.second)
        }
    }

}
