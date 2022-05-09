package com.example.inirv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.Knob.Knob
import com.example.inirv.Knob.KnobActivity
import com.example.inirv.Menu.MenuActivity
import com.example.inirv.Settings.SettingsActivity
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import com.example.inirv.Knob.HomeKnobFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_knob.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_home);

        // TODO: Remove/setup the appropriate way when moving past Investor build
        setupKnobArray()
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
        knobIntent.putExtra("angle", knobToAdd.mAngle)

        // TODO: Use startactivitywithresult while we're not grabbing the info from the backend, and potentially if it just makes sense moving forward to pass some data
//        startActivity(knobIntent);
        startActivityForResult(knobIntent, 1);
    }

    // Turn off all knobs for the user
    fun turnOffKnobs(view: View){

        // Set the state of the turnofknob boolean
        turnOffKnobsPressed = !turnOffKnobsPressed

        // Set the state of the button

    }

    // Enable safety lock
    fun enableSafetyLock(view: View){

        // Set the state of the enablesafetylock boolean
        enableSafetyLockPressed = !enableSafetyLockPressed;

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
            knobToAdd = Knob(angle = angle, angles = angles, batteryLevel = batteryLevel, macID = macID, id = id)

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
    fun rotateKnob(index: Int, angle: Double){

        // Rotate the handle/arrow of the homeknobfragment
//        homeKnobFragList[index].rotateByAngle(angle)
//        homeknob1.rotateByAngle(angle)
//        (testList[0] as HomeKnobFragment).rotateByAngle(angle);
        testList[0].findFragment<HomeKnobFragment>().rotateByAngle(angle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                knobArray[0].mAngle  = data?.getDoubleExtra("knobActivityAngle", -1.0) ?: 0.0
                rotateKnob(0, knobArray[0].mAngle)
            }
        }
    }
}