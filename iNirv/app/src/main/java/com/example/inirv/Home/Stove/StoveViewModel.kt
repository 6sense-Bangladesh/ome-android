package com.example.inirv.Home.Stove

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inirv.Knob.Knob
import com.example.inirv.managers.*
import com.example.inirv.managers.WebsocketManager.WebsocketManager
import kotlinx.coroutines.launch

class StoveViewModel(
    _sharedPreferences: SharedPreferences? = null,
    _knobManager: KnobManager = KnobManager,
    _userManager: UserManager = UserManager
) : ViewModel(), KnobManagerDelegate, StoveFragmentDelegate {

    var sharedPreferences: SharedPreferences?
        private set
    var knobManager: KnobManager
        private set
    var userManager: UserManager
        private set
    var knobs: MutableLiveData<List<Knob>>
        private set


    init {
        sharedPreferences = _sharedPreferences
        knobManager = _knobManager
        userManager = _userManager
        knobs = MutableLiveData()
    }

    fun getAllKnobs(){

        knobManager.sendRestCommand(
            commandType = RESTCmdType.ALLKNOBS,
            methodType = RESTMethodType.GET
        )
    }

    fun setupDelegates(){

        // Set the delegates for the managers
        knobManager.setDelegate(this)
    }

    fun resetAutoShutOffVars(){

        sharedPreferences?.edit()?.putBoolean("autoShutOffNotifReceived", false)?.apply()
    }

    fun getKnobAt(position: Int): Knob?{

        return knobManager.getKnobAt(position)
    }

    // MARK: KnobManagerDelegate
    override fun kmHandleResponse(
        response: MutableMap<String, Any>,
        commandType: RESTCmdType,
        methodType: RESTMethodType
    ) {

        if (commandType == RESTCmdType.ALLKNOBS &&
            methodType == RESTMethodType.GET){

            viewModelScope.launch {
                knobs.value = knobManager.knobs
            }

        } else if (
            (commandType == RESTCmdType.SAFETYLOCKOFF &&
                    methodType == RESTMethodType.POST) ||
            (commandType == RESTCmdType.SAFETYLOCKON &&
                    methodType == RESTMethodType.POST)){

        }
    }

//    override fun kmWebsocketResponse(response: MutableMap<String, Any>) {
////        TODO("Not yet implemented")
//        print("StoveVM: Websocket response received: $response")
//    }

    override fun kmWebsocketResponse(response: WebsocketManager.WebsocketResponse) {
//        TODO("Not yet implemented")
        print("StoveVM: Websocket response received: $response")
    }

    // MARK: StoveFragmentDelegate
    override fun onStart() {

        setupDelegates()
        getAllKnobs()
    }

    override fun safetyLockPressed(isOn: Boolean, macID: String) {

        val params: MutableMap<String, Any> = mutableMapOf(Pair("macID", macID))

        val commandType = if (isOn){
            RESTCmdType.SAFETYLOCKOFF
        } else {
            RESTCmdType.SAFETYLOCKON
        }

        knobManager.sendRestCommand(
            params = params,
            commandType = commandType,
            methodType = RESTMethodType.POST
        )
    }

    override fun turnOffPressed() {

        for (knob in knobManager.knobs){

            if (knob.mCurrLevel != knob.mAngles[0]){

                val params = mutableMapOf<String, Any>(
                    Pair("macID", knob.mMacID),
                    Pair("level", knob.mAngles[0].toString())
                )

                knobManager.sendRestCommand(params, RESTCmdType.KNOBANGLE, RESTMethodType.POST)
            }
        }
    }

    override fun goToScreen() {
//        TODO("Not yet implemented")
    }

    override fun getStoveOrientation(): Int {
        return userManager.user?.stoveOrientation.let { -1 }
    }

}