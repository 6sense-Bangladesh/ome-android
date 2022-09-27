package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAPassword

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthCodeDeliveryDetails
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.example.inirv.Enums.Userflow
import com.example.inirv.managers.AmplifyManager
import kotlinx.coroutines.launch

class CAPasswordViewModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String = "",
    var confirm: String = "",
    val amplifyManager: AmplifyManager = AmplifyManager,
    val userflow: Userflow = Userflow.createAccount,
    val sharedPreferences: SharedPreferences? = null,
    var emailDestination: String = ""
) : ViewModel(), CAPasswordFragmentDelegate {

    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set
    var confMessage: MutableLiveData<String> = MutableLiveData()
        private set
    private var buttonPressed: Boolean = false
    private var password: String = ""

    override fun continueBtnPressed(password: String, confirmPassword: String) {

        if (buttonPressed){
            return
        }

        buttonPressed = true

        // Check if the password is valid
        if (!isPasswordValid(password, confirmPassword)){
            setup()
            return
        }

        // Determine which action to take
        viewModelScope.launch {
            when (userflow) {

                Userflow.forgotPassword -> {
                    forgotPasswordFlow(password, confirmPassword)
                }
                Userflow.changePassword -> {
                    changePasswordFlow(password, confirmPassword)
                }
                else -> {
                    createAccountFlow(password, confirmPassword)
                }
            }
        }
    }

    override fun setup() {

        buttonPressed = false
    }

    private fun containsSpecialCharacter(sequence: String): Boolean{

        val pattern = Regex("^\\$\\*\\.\\[\\]\\{\\}\\(\\)\\?\"!@#%&/\\,<>':;|_~=+-`")

        return pattern.containsMatchIn(sequence)
    }

    private fun isPasswordValid(password: String, confirmPassword: String): Boolean{

        if(password.trim().isEmpty()){

            errorMessage.value = "Please make sure to enter a password"
            return false
        } else if (confirmPassword.trim().isEmpty()){

            errorMessage.value = "Please make sure to enter a confirmation password"
            return false
        }else if (password.length <= 8){

            errorMessage.value = "Please make sure that your password length is 9 characters or more"
            return false
        }else if (confirmPassword.length <= 8){

            errorMessage.value = "Please make sure that your confirmation password length is 9 characters or more"
            return false
        }else if (password.length > 25){

            errorMessage.value = "Please make sure that your password length is less than 26 characters"
            return false
        }else if (confirmPassword.length > 25){

            errorMessage.value = "Please make sure that your confirmation password length is less than 26 characters"
            return false
        }else if (password != confirmPassword){

            errorMessage.value = "Please make sure that your passwords match"
            return false
        }else {

            // Check for at least three out of the four cases: upper case, lower case, number, special
            var hasUpper: Boolean = false
            var hasLower: Boolean = false
            var hasNumber: Boolean = false
            var hasSpecial: Boolean = false

            for (char in password){

                if (char.isUpperCase()){
                    hasUpper = true
                } else if (char.isLowerCase()){
                    hasLower = true
                } else if (char.isDigit()){
                    hasNumber = true
                }
            }

            if (containsSpecialCharacter(password)){
                hasSpecial = true
            }

            var qualificationsCounter = 0

            if (hasUpper){
                qualificationsCounter += 1
            }
            if (hasLower){
                qualificationsCounter += 1
            }
            if (hasNumber){
                qualificationsCounter += 1
            }
            if (hasSpecial){
                qualificationsCounter += 1
            }

            if (qualificationsCounter < 3){
                errorMessage.value = "Please make sure to include at least three of the following types: upper case, lower case, number and/or special character"
                return false
            }
        }

        return true
    }

    private suspend fun createAccountFlow(password: String, confirmPassword: String){

        amplifyManager.signUp(
            email,
            password,
            phoneNumber
        ) {amplifyResultValue ->

            if(amplifyResultValue.wasCallSuccessful){

                // Determine what to do based on the next step
                when(amplifyResultValue.signUpResult?.nextStep?.signUpStep ){

                    AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> {
                        this@CAPasswordViewModel.password = password
                        this@CAPasswordViewModel.confirmUserFlow(
                            amplifyResultValue.signUpResult?.nextStep?.codeDeliveryDetails ?: AuthCodeDeliveryDetails(this@CAPasswordViewModel.email, AuthCodeDeliveryDetails.DeliveryMedium.EMAIL),
                            amplifyResultValue.signUpResult?.nextStep?.additionalInfo ?: mapOf()
                        )
                    }
                    AuthSignUpStep.DONE -> {
                        // Account was already confirmed so we just sign the user in
                        this@CAPasswordViewModel.signUserIn(password)
                    }
                }
            } else {

                amplifyResultValue.authException?.message?.contains("An account with the given email already exists")?.let {
                    val x = amplifyResultValue.authException?.message
                    errorMessage.value = "An account with the given email already exists. Please try a different email or try logging in with the given email."
                    setup()
                    return@signUp
                }

                if (amplifyResultValue.authException != null){
                    println("CAPasswordViewModel createAccountFlow authException: ${amplifyResultValue.authException}")
                }

                errorMessage.value = "Something went wrong, please try again."

                setup()
            }


        }
    }

    private fun signUserIn(password: String){

    }

    private fun forgotPasswordFlow(password: String, confirmPassword: String){

    }

    private fun changePasswordFlow(password: String, confirmPassword: String){

    }

    private fun confirmUserFlow(deliveryDetails: AuthCodeDeliveryDetails, info: Map<String, String>){

        // Setup a dictionary
        val namesToAdd = arrayOf(firstName, lastName)

        sharedPreferences?.edit()?.putString("names", namesToAdd.toString())?.apply()


        // Update the confirmation to hold the email destination
        confMessage.value = deliveryDetails.destination
    }
}