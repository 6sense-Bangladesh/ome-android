package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAConfirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.example.inirv.Enums.Userflow
import com.example.inirv.managers.AmplifyManager
import kotlinx.coroutines.launch

class CAConfirmViewModel(
    private val amplifyManager: AmplifyManager = AmplifyManager,
    private var wasConfirmationCodeSent: Boolean = true,
    private val userflow: Userflow = Userflow.createAccount,
    private var email: String = "",
    private var censoredEmail: String = "",
    val _confirmationCode: String = ""
) : ViewModel(), CAConfirmFragmentDelegate {
    // TODO: Implement the ViewModel

    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set
    var confirmWasSentMsg: MutableLiveData<String> = MutableLiveData()
        private set
    var setupMLD: MutableLiveData<Boolean> = MutableLiveData()
        private set
    var forgotPasswordFlow: MutableLiveData<Boolean> = MutableLiveData()
        private set
    var confirmationSuccessful: MutableLiveData<Boolean> = MutableLiveData()
        private set

    var confirmationCode: String = _confirmationCode
        private set

    private var actionPressed: Boolean = false

    private fun setup(){

        actionPressed = false

        if (!wasConfirmationCodeSent){

            viewModelScope.launch {
                sendConfCode()
            }

        } else {
            confirmWasSentMsg.value = "Confirmation code sent via email to: $censoredEmail \nMake sure to check your spam folder"
            setupMLD.value = true
        }
    }

    private suspend fun sendConfCode(){

        amplifyManager.resendSignUpCode(email){ amplifyResultValue ->

            // Update wasConfirmationCode
            wasConfirmationCodeSent = true

            if (amplifyResultValue.wasCallSuccessful){

                // Send out the confirmation code
                confirmWasSentMsg.value = "Confirmation code sent via email to: $censoredEmail \nMake sure to check your spam folder"
                setupMLD.value = true
            } else {

                errorMessage.value = "Something went wrong, please press 'Resend Code' to get your confirmation code"
                actionPressed = false
            }
        }
    }

    // MARK: CAConfirmFragmentDelegate
    override fun confirmPressed(confirmationCode: String) {

        if (actionPressed){
            return
        }

        actionPressed = true

        if (confirmationCode.trim().isEmpty()){

            errorMessage.value = "Please make sure to enter a confirmation code."
            actionPressed = false
        }

        this.confirmationCode = confirmationCode

        // Determine what to do based off of the userflow
        when(userflow){
            Userflow.forgotPassword -> {
                actionPressed = false
                forgotPasswordFlow.value = true
            }

            else -> {
                viewModelScope.launch {
                    amplifyManager.confirmSignUp(email, confirmationCode) { amplifyResultValue ->

                        if (amplifyResultValue.wasCallSuccessful){

                            when(amplifyResultValue.signUpResult?.nextStep?.signUpStep){

                                AuthSignUpStep.DONE -> {

                                    this@CAConfirmViewModel.confirmationCode = confirmationCode
                                    confirmationSuccessful.value = true
                                }
                                else -> {
                                    errorMessage.value = "Something went wrong, please try again."
                                }
                            }
                        } else {

                            when(amplifyResultValue.authException?.message){

                                "User cannot be confirmed. Current status is CONFIRMED"-> {

                                    this@CAConfirmViewModel.confirmationCode = confirmationCode
                                    confirmationSuccessful.value = true
                                    return@confirmSignUp
                                }
                                else ->{
                                    errorMessage.value = "The user was not confirmed, please try again"
                                }
                            }
                        }

                        actionPressed = false
                    }
                }
            }
        }
    }

    override fun resendConfCode() {

        viewModelScope.launch {
            sendConfCode()
        }
    }

    override fun onStart() {

        setup()
    }
}