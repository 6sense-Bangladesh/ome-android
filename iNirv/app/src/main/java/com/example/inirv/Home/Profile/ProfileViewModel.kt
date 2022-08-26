package com.example.inirv.Home.Profile

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inirv.managers.*
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    _sharedPreferences: SharedPreferences? = null,
    _knobManager: KnobManager = KnobManager,
    _userManager: UserManager = UserManager,
    _amplifyManager: AmplifyManager = AmplifyManager,
    _websocketManager: WebsocketManager = WebsocketManager
) : ViewModel(), ProfileFragmentDelegate, UserManagerDelegate {

    var sharedPreferences: SharedPreferences?
        private set
    var knobManager: KnobManager
        private set
    var userManager: UserManager
        private set
    var amplifyManager: AmplifyManager
        private set
    var websocketManager: WebsocketManager

    var firstName: MutableLiveData<String> = MutableLiveData()
        private set
    var lastName: MutableLiveData<String> = MutableLiveData()
        private set
    var email: MutableLiveData<String> = MutableLiveData()
        private set
    var goToScreens: MutableLiveData<ProfileGoToScreens> = MutableLiveData()
        private set
    var errorMessage: MutableLiveData<String> = MutableLiveData()
        private set

    init {
        sharedPreferences = _sharedPreferences
        knobManager = _knobManager
        userManager = _userManager
        amplifyManager = _amplifyManager
        websocketManager = _websocketManager
    }

    fun setup(){

        firstName.value = userManager.user?.firstName?: ""
        lastName.value = userManager.user?.lastName?: ""
        email.value = userManager.user?.email?: "Email"
    }

    fun updateDevTokens(){

        sharedPreferences?.getString("omePreferences", "deviceToken")?.let { deviceToken ->

            if (userManager.user?.deviceTokens?.contains(deviceToken) == true){

                // Remove device token from the list of device tokens
                var deviceTokenList = userManager.user!!.deviceTokens.toMutableList()
                deviceTokenList.remove(deviceToken)

                // Convert the list to a string of device tokens
                val deviceTokenListString = deviceTokenList.joinToString(",")

                // Make the call to update the user on the backend
                val params = mapOf<String, String>(Pair("deviceTokens", deviceTokenListString))
                userManager.updateUserProfile(params)
            }
        }
    }

    fun saveSharedPreferences(){

        // Store shared preferences
        val deviceToken = sharedPreferences?.getString("deviceToken", "")
        val connectedToWifi = sharedPreferences?.getString("connectedToWifi", "")
        val publicIPAddress = sharedPreferences?.getString("publicIPAddress", "")
        val nextTime = sharedPreferences?.getBoolean("nextTime", false)
        val versionToSkip = sharedPreferences?.getString("versionToSkip", "")
        val names = sharedPreferences?.getString("names", "")

        // Clear shared preferences (all the ones we didnt need to save)
        sharedPreferences?.edit()?.clear()

        // Add back in the saved shared preferences
        sharedPreferences?.edit()?.putString("deviceToken", deviceToken)
        sharedPreferences?.edit()?.putString("connectedToWifi", connectedToWifi)
        sharedPreferences?.edit()?.putString("publicIPAddress", publicIPAddress)
        sharedPreferences?.edit()?.putBoolean("nextTime", nextTime?: false)
        sharedPreferences?.edit()?.putString("versionToSkip", versionToSkip)
        sharedPreferences?.edit()?.putString("names", names)

        sharedPreferences?.edit()?.commit()
    }

    fun logoutUserHandler(resultValue: AmplifyResultValue){

        if (resultValue.wasCallSuccessful){

            userManager.removeDelegate(this)
            knobManager.removeAllKnobs()
            websocketManager.disconnectFromWebsocket()

            goToScreens.value = ProfileGoToScreens.logout
        } else {

            errorMessage.value = "There was an error when logging out"
        }
    }

    fun updateUserProfile(firstName: String, lastName: String){

        val params = mapOf(Pair("firstName", firstName), Pair("lastName", lastName))
        userManager.updateUserProfile(params)
    }

    // MARK: ProfileFragmentDelegate
    override fun logoutUserPressed() {

        // Save the shared preferences
        saveSharedPreferences()

        // Logout the user
        viewModelScope.launch {

            amplifyManager.signUserOut { amplifyResultValue ->

                logoutUserHandler(amplifyResultValue)
            }
        }
    }

    override fun getName(): Array<String> {

        return arrayOf(firstName.value ?: "", lastName.value ?: "")
    }

    override fun updateName(firstName: String, lastName: String) {
//        TODO("Not yet implemented")
        print("")
    }

    override fun onStart(){

        setup()
    }

    // MARK: Usermanager Delegate
    override fun umHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {
//        TODO("Not yet implemented")
    }

    override fun umWebsocketResponse(response: MutableMap<String, Any>) {
//        TODO("Not yet implemented")
    }
}