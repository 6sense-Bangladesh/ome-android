package com.example.inirv

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.Knob.KnobActivity
import com.example.inirv.Menu.MenuActivity
import com.example.inirv.Settings.SettingsActivity

class HomeActivity : AppCompatActivity(){

    // Variables
    var turnOffKnobsPressed: Boolean = false;   // Boolean to keep track of the state of the turnofknob button
    var enableSafetyLockPressed: Boolean = false // Boolean to keep track of the state for the enable safety lock button
    var knobPressed: Boolean = false    // Boolean to keep track of when a knob is pressed
    var menuPressed: Boolean = false    // Boolean for the menu button being pressed
    var settingsPressed: Boolean = false    // Boolean to keep track of the settings button being pressed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_home);
    }

    override fun onResume() {
        super.onResume()

        // Reset necessary button values
        knobPressed = false;
        menuPressed = false;
        settingsPressed = false
    }

    // Take the appropriate action when a knob is clicked on
    fun knobPressed(view: View){

        // Check if a knob has already been clicked
        if (knobPressed) {
            return;
        }

        // Set the knob pressed value
        knobPressed = true

        // Go to the knobview activity
        val knobIntent : Intent = Intent(this, KnobActivity::class.java)
        startActivity(knobIntent);
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

        // Check if the settings button has been pressed
        if(settingsPressed){
            return
        }

        // Set the settings button boolean
        settingsPressed = true;

        // Go to the Menu activity
        val settingsIntent : Intent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent);

        // Make the settings view slide in from the right
        overridePendingTransition(R.anim.slide_right_in, R.anim.nav_default_pop_exit_anim)
    }

}