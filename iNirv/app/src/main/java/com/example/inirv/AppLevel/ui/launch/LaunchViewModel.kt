package com.example.inirv.AppLevel.ui.launch

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.example.inirv.AppLevel.AppNavigatorScreen
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.managers.*
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import kotlinx.coroutines.launch

class LaunchViewModel(
    _userManager: UserManager? = UserManager,
    _knobManager:KnobManager? = KnobManager,
    _amplifyManager: AmplifyManager? = AmplifyManager,
    override var onFinished: ((CoordinatorInteractor) -> Unit)?,
    _sharedPreferences: SharedPreferences?,
//    _screen: AppNavigatorScreen = AppNavigatorScreen.welcome
) : ViewModel(), CoordinatorInteractor, UserManagerDelegate, KnobManagerDelegate {

    private val userManager: UserManager?
    private val knobManager:KnobManager?
    private val amplifyManager: AmplifyManager?
    private val sharedPreferences: SharedPreferences?
    var screen: MutableLiveData<AppNavigatorScreen> = MutableLiveData()
//    var screen: AppNavigatorScreen = AppNavigatorScreen.welcome
//        private set

    // Initializer
    init {
        userManager = _userManager
        knobManager = _knobManager
        amplifyManager = _amplifyManager
        sharedPreferences = _sharedPreferences
//        screen = _screen
    }

    fun setup(){

        userManager?.setDelegate(this)
        knobManager?.setDelegate(this)

        /**
         * TODO: Need to setup the network management
         * For now just call the checksignin status here
         */
        checkSignInStatus()
//        signUserOut()
    }

    // Check signin status
    fun checkSignInStatus(){

        viewModelScope.launch {
            amplifyManager?.fetchAuthSession { amplifyResultValue ->

                processCheckSignInStatusResults(amplifyResultValue)
            }
        }
    }

    fun processCheckSignInStatusResults(resultValue: AmplifyResultValue){

        if (resultValue.wasCallSuccessful){

            // Set the app configuration key to true
            sharedPreferences?.edit()?.putBoolean("appConfigure", true)?.apply()

            // Set the setup first device key to false, to setup if the user needs to go
            // through the initial setup a new device process
            sharedPreferences?.edit()?.putBoolean("setupFirstDevice", false)?.apply()

            // Determine where to go based on the current sign in status
            if (resultValue.session!!.isSignedIn){

                /** If the user is signed in, grab the cognito token and give that to the user
                 * to set up the restmanager appropriately
                 */
                var cognitoAccessToken = ""
                if (resultValue.session is AWSCognitoAuthSession) {

                    cognitoAccessToken = (resultValue.session as AWSCognitoAuthSession)
                        .userPoolTokens.value!!.accessToken
                }

                viewModelScope.launch {
                    // Setup the User Manager
                    userManager?.setup()
                }

            } else {

                // The user is logged out so tell the coordinator to determine where to go next
                screen.value = AppNavigatorScreen.welcome
//                onFinished?.invoke(this)
            }

        } else {

            /** TODO: Need to determine what to do in case of some error
             * Probably just keep attempting to grab the aws session
             */
            println("Error fetching the user session, error: ${resultValue.authException}")
        }
    }

    // Sign User Out
    fun signUserOut(){

        viewModelScope.launch {

            // Sign the user out and then notifiy the coordinator the view model is finished
            amplifyManager?.signUserOut { amplifyResultValue ->

                signUserOutHandler(amplifyResultValue)
            }
        }
    }

    fun  signUserOutHandler(amplifyResultValue: AmplifyResultValue){

        if (amplifyResultValue.wasCallSuccessful){
            // User was signed out successfully so update what screen to go to
            screen.value = AppNavigatorScreen.welcome
//            onFinished?.invoke(this@LaunchViewModel)
        } else {
            /**
             * TODO: Need to determine what to do here in the case of an error
             */
            println("Error signing the user out error: ${amplifyResultValue.authException}")
        }
    }

    // MARK: KnobManagerDelegate
    override fun kmHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        viewModelScope.launch {
            screen.value = if (userManager?.user?.numKnobs!! < 1 && userManager.user?.stoveSetupComplete!!){

                AppNavigatorScreen.knobInstallationOne
            } else {

                AppNavigatorScreen.home
            }
        }

//        viewModelScope.launch {
//            onFinished?.invoke(this@LaunchViewModel)
//        }
    }

    override fun kmWebsocketResponse(response: WebsocketManager.WebsocketResponse) {
//        TODO("Not yet implemented")
    }

//    override fun kmWebsocketResponse(response: MutableMap<String, Any>) {
//        TODO("Not yet implemented")
//    }


    // MARK: UserManagerDelegate
    override fun umHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        // Check out if there are error messages
        for ((key, value) in response){

            when (key == "error"){
                (value == "User doesn't exist"), (value == "Generic fetch user error") -> {

                    signUserOut()
                    return
                }
            }
        }

        // Go through the userflow check
        if ((commandType == RESTCmdType.USER) && (methodType == RESTMethodType.GET)){
            userFlowCheck()
            return
        }
    }

    override fun umWebsocketResponse(response: MutableMap<String, Any>) {
//        TODO("Not yet implemented")
    }

    fun userFlowCheck(){

        // Tell the userprofile manager to update it's device list
        if (sharedPreferences != null && userManager != null) {
            val deviceToken = sharedPreferences.getString("omePreferences", "deviceToken")

            userManager.user?.deviceTokens?.let {

                if (!userManager.user?.deviceTokens!!.contains(deviceToken) && deviceToken != null){

                    val deviceTokenList = userManager.user?.deviceTokens?.toMutableList()

                    deviceTokenList?.add(deviceToken)

                    val deviceTokenString = deviceTokenList?.joinToString(separator = ",")

                    val params = mapOf<String, String>(Pair("deviceTokens", deviceTokenString!!))
                    userManager.updateUserProfile(params = params)
                }
            }
        }

        if (userManager != null){

            // Check if the stove has been setup
            if(userManager.user?.stoveOrientation == -1){

                sharedPreferences?.edit()?.putBoolean("appConfigure", false)?.apply()
                sharedPreferences?.edit()?.putBoolean("setupFirstDevice", true)?.apply()

                // Tell the coordinator to go to a new screen
//                screen.value = AppNavigatorScreen.stoveBrand
                screen.postValue(AppNavigatorScreen.stoveBrand)

//                onFinished?.invoke(this)
                return
            } else {

                if (userManager.user?.numKnobs!! < 1){

                    sharedPreferences?.edit()?.putBoolean("setupFirstDevice", true)?.apply()

                    screen.value = if (userManager.user?.stoveSetupComplete!!){
                        AppNavigatorScreen.knobInstallationOne
                    } else {
                        AppNavigatorScreen.home
                    }

//                    onFinished?.invoke(this)
                    return
                }
            }

            knobManager?.setDelegate(this)
            knobManager?.setup()
        }

    }


}