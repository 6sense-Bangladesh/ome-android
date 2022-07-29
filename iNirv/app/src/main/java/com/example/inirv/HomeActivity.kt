package com.example.inirv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.example.inirv.Knob.HomeKnobFragment
import com.example.inirv.Knob.Knob
import com.example.inirv.Knob.KnobActivity
import com.example.inirv.Menu.MenuActivity
import com.example.inirv.managers.AmplifyManager
import com.example.inirv.managers.RESTManager
import com.example.inirv.managers.UserManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(){

    // Variables
    var knobArray: MutableList<Knob> = mutableListOf<Knob>()
    var homeKnobFragList: MutableList<HomeKnobFragment> = mutableListOf<HomeKnobFragment>()
    var testList: MutableList<FragmentContainerView> = mutableListOf<FragmentContainerView>()

    var turnOffKnobsPressed: Boolean = false;   // Boolean to keep track of the state of the turnofknob button
    var enableSafetyLockPressed: Boolean = false // Boolean to keep track of the state for the enable safety lock button
    var knobPressed: Boolean = false    // Boolean to keep track of when a knob is pressed
    var menuPressed: Boolean = false    // Boolean for the menu button being pressed
    var settingsPressed: Boolean = false    // Boolean to keep track of the settings button being pressed

    // TODO: Remove when done testing
    companion object {
        //  You define a companion object to hold the API endpoint (URL),
        //  a search term and a concatenated string of the two.
        private const val URL = "https://api.github.com/search/repositories"
        //    private const val SEARCH = "q=super+mario+language:kotlin&sort=stars&order=desc"
        private const val SEARCH = "q=language:kotlin&sort=stars&order=desc&?per_page=50"
        private const val COMPLETE_URL = "https://app-dev.api.omekitchen.com/user"//"$URL?$SEARCH"
    }
    // End of Remove

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_home);

        // TODO: Remove when done with RESTmanager tests
//        //  You execute the actual request using readText().
////        doAsync {
////            val builder = Uri.Builder()
////            builder.scheme("https")
////                .authority("app-dev.api.omekitchen.com")
////                .appendPath("user")
////                .appendQueryParameter("x-inirv-auth", "eyJraWQiOiJTc1ZGUmkxaWlXWGljUnFFTFE1UFFucm9kTDNqM0tLaUlKWEljU0VpYkl3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI5Yjc1Zjc5MS03MjM2LTQ0YmEtOGJkOC01ZTMyZDYwOGJjYWYiLCJldmVudF9pZCI6IjJkMTczMzkzLWE3MmUtNDM2Ni04ZDMwLWYwNTEzOTYwODZmMyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE2NTI0NzQ3OTEsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTIuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0yX2twaEE0Q1RURSIsImV4cCI6MTY1MjcyMzM1NSwiaWF0IjoxNjUyNzE5NzU1LCJqdGkiOiI1MTIxMzFlMi0xM2YzLTQ1MGItYWMzMS1jN2QyMDJlODVmOWQiLCJjbGllbnRfaWQiOiIxZnAyMzg2Ym5nYTY2MW03ZDdudjV2aTd2IiwidXNlcm5hbWUiOiI5Yjc1Zjc5MS03MjM2LTQ0YmEtOGJkOC01ZTMyZDYwOGJjYWYifQ.TnxV5biMgdVKu9c647CwIHfzTv-TRWnPkP0nK_FpoY7uWacXRgo8LdvWYHYqGckszgvDeiEBjtqHAni1VWl8zy15iZxolppBQn-YzpZS-RYyzzYTuHbQZSryOO1BFlMwz0HlVLruAkxUDh20oA3T1AV7SeeLMJ5DEaW-PX443Q0fBEskDHOMlWwE-9b75f791-7236-44ba-8bd8-5e32d608bcaf")
////                .appendQueryParameter("x-inirv-vsn", "6")
////                .appendQueryParameter("x-inirv-uid", "9b75f791-7236-44ba-8bd8-5e32d608bcaf")
////            val repoListJsonStr = java.net.URL(builder.toString()).readText()
////            Log.d("RestManagerTests", "URL: $repoListJsonStr")
////        }
//        // TODO: EoR
//
//        // TODO: Remove when done with AmplifyManager Tests
//        try {
//            testAPICommands()
//        } catch (error: Error){
//            Log.v("AmplifyManager", "Error running the test API command")
//        }
        // TODO: EoR


        // TODO: Remove/setup the appropriate way when moving past Investor build
        setupKnobArray()
    }

    fun testAPICommands(){

//        var amplifyManager = AmplifyManager

//        amplifyManager.fetchAuthSession()
//        amplifyManager.signUserIn("tjgriffi357+3@gmail.com", "Tester123?")
//        amplifyManager.signUserOut()

        var restManager = RESTManager

    }

    override fun onResume() {
        super.onResume()

        // Reset necessary button values
        knobPressed = false;
        menuPressed = false;
        settingsPressed = false
    }

    override fun onStart() {
        super.onStart()

    }

    // Take the appropriate action when a knob is clicked on
    fun knobPressed(view: View){

        // Check if a knob has already been clicked
        if (knobPressed) {
            return;
        }

        // Set the knob pressed value
        knobPressed = true

        //TODO: Get the actual knob that was touched, but for now, we'll just do spot 1
        // Get the appropriate knob associated with this view
        val knobToAdd = knobArray[0];

        // Go to the knobview activity
        val knobIntent : Intent = Intent(this, KnobActivity::class.java)

        // TODO: Make sure to send over all the necessary information for the knob
        // Set the appropriate variables for the KnobActivity
        knobIntent.putExtra("angle", knobToAdd.mCurrLevel)

        // TODO: Use startactivitywithresult while we're not grabbing the info from the backend, and potentially if it just makes sense moving forward to pass some data
//        startActivity(knobIntent);
        startActivityForResult(knobIntent, 1);
    }

    // Turn off all knobs for the user
    fun turnOffKnobs(view: View){


        var amplifyManager = AmplifyManager

        lifecycleScope.launch {
//            amplifyManager.signUserIn("tjgriffi357+2@gmail.com", "Tester123@"){
//
//            }

//            amplifyManager.signUserOut {
//                println("User is signed out")
//            }
            amplifyManager.fetchAuthSession{ resultValue ->
                println("resultValue Session: ${resultValue.session}")

                if (resultValue.wasCallSuccessful){
                    Log.i("AmplifyQuickstart", "Auth session: ${resultValue.session}")
                    (resultValue.session as? AWSCognitoAuthSession)?.let { authSession ->

                        if (authSession.isSignedIn) {


                            val tokens = authSession.userPoolTokens

                            val cognitoUserPoolTokens = tokens.value?.let { tokens.value }

                            val accessToken = cognitoUserPoolTokens!!.accessToken?.let {
                                cognitoUserPoolTokens!!.accessToken
                            }

                            val userID = authSession.userSub.value?.let { userID ->
                                RESTManager.setUserID(userID)
                            }
                        } else {
                            println("User is signed out")
                        }

                    }
                } else {
                    Log.e("AmplifyQuickstart", "Failed to fetch auth session", resultValue.authException)
                }
            }
        }


        // Set the state of the turnofknob boolean
        // TODO: Uncomment when done testing
//        turnOffKnobsPressed = !turnOffKnobsPressed
        // TODO: EoUn
        // Set the state of the button

    }

    // Enable safety lock
    fun enableSafetyLock(view: View){

        UserManager.sendRestCmd()
        // Set the state of the enablesafetylock boolean
        //TODO: Undo comments when done with rest tests
//        enableSafetyLockPressed = !enableSafetyLockPressed;

        // Set the state of the enable safety lock button
    }

    // Bring out the menu activity
    fun menuButtonPressed(view: View){

        // Check if the menu button has been pressed
        if (menuPressed){
            return
        }

        menuPressed = true

        // Go to the Menu activity
        val menuIntent : Intent = Intent(this, MenuActivity::class.java)
        startActivity(menuIntent);

        // Make the view slide in from the left
        overridePendingTransition(R.anim.slide_left_in, R.anim.nav_default_pop_exit_anim)
    }

    // Bring out the settings activity
    fun settingsButtonPressed(view: View){

        // Setup the array that holds the home knob fragments
        setupHomeKnobFragList()

        // Check if the settings button has been pressed
//        if(settingsPressed){
//            return
//        }
//
//        // Set the settings button boolean
//        settingsPressed = true;
//
//        // Go to the Menu activity
//        val settingsIntent : Intent = Intent(this, SettingsActivity::class.java)
//        startActivity(settingsIntent);
//
//        // Make the settings view slide in from the right
//        overridePendingTransition(R.anim.slide_right_in, R.anim.nav_default_pop_exit_anim)
    }

    // Function to setup the array of knobs
    fun setupKnobArray(){

        var knobToAdd: Knob?
        var angle = 0.0
        var angles = intArrayOf(0, 90, 180, 270)
        var batteryLevel = 100
        var macID: String?
        var id: String?

        // TODO: For now it's six but base this off of the screen orientation
        for (knobIndex in 0..5){

            // Setup the knob with the appropriate variables
            macID = "Test $knobIndex"
            id = "Test $knobIndex"
            knobToAdd = Knob(
                macID = "",
                firmwareVersion = "",
                ipAddress = "",
                safetyLockEnabled = false,
                stoveID = "",
                stovePosition = -1,
                userID = ""
            )

            // Add the knob to the array of knobs
            knobArray.add(knobToAdd);
        }
    }

    // TODO: Need to base this off of the stove orientation
    // Function to setup the home knob fragment array
    fun setupHomeKnobFragList(){

        testList.add(homeknob1)
        // 6 burner orientation
//        homeKnobFragList.add(FragmentManager.findFragment(homeknob1))
//        homeKnobFragList.add(FragmentManager.findFragment(homeknob1))
//        homeKnobFragList.add(FragmentManager.findFragment(homeknob1))
//        homeKnobFragList.add(fragmentManager.findFragmentByTag("homeKnob1") as HomeKnobFragment)
//        homeKnobFragList.add(fragmentManager.findFragmentByTag("homeKnob1") as HomeKnobFragment)
//        homeKnobFragList.add(fragmentManager.findFragmentByTag("homeKnob1") as HomeKnobFragment)

    }

    // Function to rotate the knob
    fun rotateKnob(index: Int, angle: Int){

        // Rotate the handle/arrow of the homeknobfragment
//        homeKnobFragList[index].rotateByAngle(angle)
//        homeknob1.rotateByAngle(angle)
//        (testList[0] as HomeKnobFragment).rotateByAngle(angle);
        testList[0].findFragment<HomeKnobFragment>().rotateByAngle(angle.toDouble())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                knobArray[0].mCurrLevel  = data?.getIntExtra("knobActivityAngle", -1) ?: 0
                rotateKnob(0, knobArray[0].mCurrLevel)
            }
        }
    }
}