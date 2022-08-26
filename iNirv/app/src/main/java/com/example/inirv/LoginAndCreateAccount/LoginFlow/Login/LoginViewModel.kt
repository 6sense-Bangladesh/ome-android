package com.example.inirv.LoginAndCreateAccount.LoginFlow.Login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.managers.*
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import kotlinx.coroutines.launch

class LoginViewModel(
    override var onFinished: ((CoordinatorInteractor) -> Unit)?,
    _loginSuccess: Boolean = false,
    _goToConfirmation: Boolean = false,
    _forgotPasswordButtonPressed: Boolean = false,
    _actionPressed: Boolean = false,
    _userManager: UserManager = UserManager,
    _knobManager: KnobManager = KnobManager,
    _amplifyManager: AmplifyManager = AmplifyManager,
    _sharedPreferences: SharedPreferences? = null
) : ViewModel(), CoordinatorInteractor, LoginFragmentDelegate, UserManagerDelegate, KnobManagerDelegate {

    var loginSuccess: Boolean
        private set
    var goToConfirmation: Boolean
        private set
    var forgotPasswordButtonPressed: Boolean
        private set
    var userManager: UserManager
        private set
    var knobManager: KnobManager
        private set
    var amplifyManager: AmplifyManager
        private set
    private val sharedPreferences: SharedPreferences?
    var errorMessageLiveData: MutableLiveData<String> = MutableLiveData<String>()
        private set
    var actionPressed: Boolean
        private set
    var loginScreens: MutableLiveData<LoginGoToScreens> = MutableLiveData()
        private set

    init {
        loginSuccess = _loginSuccess
        goToConfirmation = _goToConfirmation
        forgotPasswordButtonPressed = _forgotPasswordButtonPressed
        actionPressed = _actionPressed
        userManager = _userManager
        knobManager = _knobManager
        amplifyManager = _amplifyManager
        sharedPreferences = _sharedPreferences
    }

    fun setup(){

        userManager.setDelegate(this)
        knobManager.setDelegate(this)


    }

    private fun fetchUserAuthSession(){

        viewModelScope.launch {
            amplifyManager.fetchAuthSession { amplifyResultValue ->
                processFetchUserAuthSessionResults(amplifyResultValue)
            }
        }

    }

    fun processFetchUserAuthSessionResults(amplifyResultValue: AmplifyResultValue){

        if (amplifyResultValue.wasCallSuccessful){

            if (amplifyResultValue.session!!.isSignedIn){

                /** If the user is signed in, grab the cognito token and give that to the user
                 * to set up the restmanager appropriately
                 */
                var cognitoAccessToken = ""
                if (amplifyResultValue.session is AWSCognitoAuthSession) {
                    cognitoAccessToken = (amplifyResultValue.session as AWSCognitoAuthSession)
                        .userPoolTokens.value!!.accessToken
                }

                viewModelScope.launch {
                    // Setup the User Manager
                    userManager.setup(cognitoAccessToken)
                }
            }
        } else {
            errorMessageLiveData.value = "Something went wrong. Please try again"
        }
    }

    fun loginFlow(userName: String, password: String){

        val checkLoginInfoReturnValue = checkLoginInfo(userName, password)
        if ( checkLoginInfoReturnValue == "No errors detected"){

            viewModelScope.launch {
                amplifyManager.signUserIn(userName, password){ amplifyResultValue ->
                    processLoginResults(amplifyResultValue)
                }
            }
        } else {

            errorMessageLiveData.value = checkLoginInfoReturnValue
        }
    }

    fun checkLoginInfo(userName: String, password: String): String{

        var toReturn: String = "No errors detected"

        if (userName.isEmpty()){
            toReturn = "Please make sure to enter an email"
        } else if (password.isEmpty()){
            toReturn = "Please make sure to enter a password"
        }

        return toReturn
    }

    fun processLoginResults(amplifyResultValue: AmplifyResultValue){

        if (amplifyResultValue.wasCallSuccessful){

            when(amplifyResultValue.message){
                "Confirm signup" -> {
//                    goToConfirmation = true
//                    onFinished?.invoke(this)
                    loginScreens.value = LoginGoToScreens.caConfirm
                }
                "Done" -> fetchUserAuthSession()
                else -> {
                    errorMessageLiveData.value = "Please make sure your email and password are correct"
                }
            }

        } else {

            var errorMessage: String = "Please make sure your email and password are correct"

            when(amplifyResultValue.authException?.message){
                "Incorrect username or password" -> errorMessage = "Incorrect username or password"
            }

            errorMessageLiveData.value = errorMessage
        }
    }

    fun forgotPasswordFlow(){

        if (actionPressed){
            return
        }

        actionPressed = true

//        forgotPasswordButtonPressed = true
//        onFinished?.invoke(this)
        loginScreens.value = LoginGoToScreens.forgotPassword
    }

    fun resetActionVar(){
        actionPressed = false
        loginSuccess = false
        forgotPasswordButtonPressed = false
        goToConfirmation = false
    }



    // MARK: LoginFragmentDelegate
    override fun loginFragmentButtonPressed(
        whichButton: LoginFragmentButton,
        userName: String,
        password: String
    ) {

        when(whichButton){
            LoginFragmentButton.login -> loginFlow(userName, password)
            LoginFragmentButton.forgotPassword -> forgotPasswordFlow()
        }
    }

    override fun onStart() {

        resetActionVar()

        setup()
    }

    // MARK: UserManagerDelegate
    override fun umHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        if(commandType == RESTCmdType.USER && methodType == RESTMethodType.GET){

            if (userManager.stoveOrientation == -1){

                sharedPreferences?.edit()?.putBoolean("appConfigure", false)?.apply()
                sharedPreferences?.edit()?.putBoolean("setupFirstDevice", true)?.apply()
            } else if (userManager.numKnobs < 1){

                sharedPreferences?.edit()?.putBoolean("setupFirstDevice", true)?.apply()
            } else {

                // Setup the knobmanager
                knobManager.setup()
                knobManager.setDelegate(this)
                return
            }

//            loginSuccess = true
//            onFinished?.invoke(this)
            viewModelScope.launch {
                loginScreens.value = LoginGoToScreens.parentNavigator
            }

            return
        } else if (commandType == RESTCmdType.NONE && methodType == RESTMethodType.NONE){

            viewModelScope.launch {
                if (response["error"] != null){
                    errorMessageLiveData.value =
                        response["error"] as String
                }
            }
        }
    }

    override fun umWebsocketResponse(response: MutableMap<String, Any>) {
//        TODO("Not yet implemented")
    }

    // MARK: KnobManagerDelegate
    override fun kmHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        if (commandType == RESTCmdType.ALLKNOBS && methodType == RESTMethodType.GET){

            if(userManager.numKnobs < 1){
                sharedPreferences?.edit()?.putBoolean("setupFirstDevice", true)?.apply()
            }

//            loginSuccess = true
//            onFinished?.invoke(this)
            viewModelScope.launch {
                loginScreens.value = LoginGoToScreens.parentNavigator
            }

            return
        }
    }

//    override fun kmWebsocketResponse(response: MutableMap<String, Any>) {
////        TODO("Not yet implemented")
//    }

    override fun kmWebsocketResponse(response: WebsocketManager.WebsocketResponse) {
//        TODO("Not yet implemented")
    }

}