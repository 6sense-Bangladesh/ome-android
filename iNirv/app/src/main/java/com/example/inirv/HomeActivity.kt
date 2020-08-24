package com.example.inirv

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.Knob.KnobActivity
import com.example.inirv.Menu.MenuActivity

class HomeActivity : AppCompatActivity(){

    // Variables
    var turnOffKnobsPressed: Boolean = false;   // Boolean to keep track of the state of the turnofknob button
    var enableSafetyLockPressed: Boolean = false // Boolean to keep track of the state for the enable safety lock button
    var knobPressed: Boolean = false    // Boolean to keep track of when a knob is pressed
    var menuPressed: Boolean = false    // Boolean for the menu button being pressed

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

    // Bring out the menu fragment
    fun menuButtonPressed(view: View){

        if (menuPressed){
            return
        }

        menuPressed = true

        // Go to the Menu activity
        val menuIntent : Intent = Intent(this, MenuActivity::class.java)
        startActivity(menuIntent);

    }

}