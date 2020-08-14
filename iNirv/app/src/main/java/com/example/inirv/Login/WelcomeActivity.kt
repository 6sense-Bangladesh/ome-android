package com.example.inirv.Login

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.R

class WelcomeActivity: AppCompatActivity() {

    // Variables
    var loginPressed: Boolean = false
    var setupAccountPressed: Boolean = false

    // On Create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup the layout for this activity
        setContentView(R.layout.activity_welcome)
    }

    //
    override fun onResume() {
        super.onResume()

        // Set the button press vars to false
        loginPressed = false;
        setupAccountPressed = false;
    }

    // Login Account Page onClick
    fun loginPressed(view: View){

        // Check if the buttons were already pressed
        if ( checkButtonsAlreadyPressed() ) {
            return
        }

        // Set the button pressed variables
        loginPressed = true

        // Setup up the intent and start up the new activity
        val loginIntent : Intent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent);
    }

    // Setup Account Page onClick
    fun setupAccountPressed(view: View){

        // Check if the buttons were already pressed
        if ( checkButtonsAlreadyPressed() ) {
            return
        }

        // Set the button pressed variables
        setupAccountPressed = true

        // Setup up the intent and start up the new activity
        val setupAccountIntent : Intent = Intent(this, SetupAccountActivity::class.java)
        startActivity(setupAccountIntent);
    }

    // Check for the button pressed booleans
    fun checkButtonsAlreadyPressed(): Boolean = loginPressed || setupAccountPressed
}