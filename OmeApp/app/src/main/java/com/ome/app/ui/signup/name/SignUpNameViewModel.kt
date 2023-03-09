package com.ome.app.ui.signup.name

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpNameViewModel @Inject constructor() : BaseViewModel() {

    val firstAndLastNameValidationLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun validateFirstAndLastName(firstName: String, lastName: String) {
        if (firstName.isEmpty()) {
           defaultErrorLiveData.postValue("Please make sure to enter a first name.")
            return
        }
        if (lastName.isEmpty()) {
           defaultErrorLiveData.postValue("Please make sure to enter a last name.")
            return
        }
        firstAndLastNameValidationLiveData.postValue(true)
    }

}
