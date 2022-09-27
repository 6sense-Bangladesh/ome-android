package com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CASuccess

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.example.inirv.managers.*
import kotlinx.coroutines.launch

class CASuccessViewModel(
    val email: String,
    val password: String,
    val userManager: UserManager = UserManager,
    val amplifyManager: AmplifyManager = AmplifyManager
) : ViewModel(), CASuccessFragmentDelegate, UserManagerDelegate {

    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set
    var userManagerSetupComplete: MutableLiveData<Boolean> = MutableLiveData()
        private set

    var actionPressed: Boolean = false

    private fun setup(){

        actionPressed = false
        userManager.setDelegate(this)
    }

    private suspend fun signUserIn(){

        amplifyManager.signUserIn(email, password){ amplifyResultValue ->

            if (amplifyResultValue.wasCallSuccessful){

                when(amplifyResultValue.signInResult?.nextStep?.signInStep){
                    AuthSignInStep.DONE -> {

                        viewModelScope.launch {
                            userManager.setup()
                        }
                    }
                    else -> {
                        setup()
                        errorMessage.value = "Something went wrong with the signin process.  Please try again"
                    }
                }
            } else {

                setup()
                errorMessage.value = amplifyResultValue.authException?.message
            }
        }
    }

    // MARK: CASuccessFragmentDelegate
    override fun startSetupBtnPressed() {

        if (actionPressed){
            return
        }

        actionPressed = true

        viewModelScope.launch {
            signUserIn()
        }
    }

    override fun onStart() {
        setup()
    }

    // MARK: UserManagerDelegate
    override fun umHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        if (commandType == RESTCmdType.USER && methodType == RESTMethodType.GET){

            // Say the user manager has already been setup
//            userManagerSetupComplete.value = true
            userManagerSetupComplete.postValue(true)
        } else if (commandType == RESTCmdType.NONE && methodType == RESTMethodType.NONE){

            if (response["error"] != null){

                setup()
                errorMessage.value = (response["error"] as String)
            }
        }
    }

    override fun umWebsocketResponse(response: MutableMap<String, Any>) {
        return
    }
}