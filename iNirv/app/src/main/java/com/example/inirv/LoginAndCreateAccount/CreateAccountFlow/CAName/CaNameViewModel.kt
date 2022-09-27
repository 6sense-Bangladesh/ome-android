package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAName

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaNameViewModel(
    _firstName: String = "",
    _lastName: String = "",
    _errorMessage: String = ""
) : ViewModel(), CaNameFragmentDelegate {

    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set
    var firstName: MutableLiveData<String> = MutableLiveData()
        private set
    var lastName: MutableLiveData<String> = MutableLiveData()
        private set
    private var actionPressed: Boolean = false

    init {

//        firstName = MutableLiveData(_firstName)
//        lastName = MutableLiveData(_lastName)
//        errorMessage = MutableLiveData(_errorMessage)
    }

    fun setup(){

        actionPressed = false
    }

    override fun continueBtnPressed(firstName: String, lastName: String) {

        if(actionPressed){
            return
        }

        actionPressed = true

        if (firstName.trim().isEmpty()){

            this.errorMessage.value = "Please make sure to enter a first name"
            setup()
        } else if (lastName.trim().isEmpty()){

            this.errorMessage.value = "Please make sure to enter a last name"
            setup()
        } else{

            this.firstName.value = firstName
            this.lastName.value = lastName
        }
    }
}