package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAContact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class CAContactViewModel : ViewModel(), CAContactFragmentDelegate {

    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set
    var email: MutableLiveData<String> = MutableLiveData()
        private set
    var phoneNumber: String = ""
        private set

    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private var buttonPressed: Boolean = false

    fun setup(){

        buttonPressed = false
    }

    private fun isValidEmailString(email: String): Boolean{

        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    // MARK: CAContactFragmentDelegate
    override fun continueBtnPressed(email: String, phoneNumber: String) {


        // Check that the email isnt empty
        if(email.trim().isEmpty()){
            errorMessage.value = "Please make sure to enter an email"
        }else if(!isValidEmailString(email)){
            errorMessage.value = "Please make sure youre using a valid email"
        } else {
            /**
             * TODO: Need to properly format the phone number
             * For now just make it empty
             */

            this.phoneNumber = /*phoneNumber*/ ""
            this.email.value = email
        }
    }

    override fun getInfo(): Map<String, String> {

        val infoMap: Map<String, String> = mapOf(
            Pair("email", email.value!!),
            Pair("phoneNumber", phoneNumber)
        )

        return infoMap
    }
}