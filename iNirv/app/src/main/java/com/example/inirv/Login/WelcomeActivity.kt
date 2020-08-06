package com.example.inirv.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.R

// Activity that takes the user to the login or create account screens
class WelcomeActivity: AppCompatActivity() {

    var loginPressed : Boolean = false
    var setupPressed : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){

        // Run the super for oncreate
        super.onCreate(savedInstanceState)

        // Set the layout for this Activity
        setContentView(R.layout.activity_welcome)
    }

    fun loginPressed(view: View){
        // Go to the login page

        // Check if the login/setup button has already been pressed
        if (checkIfButtonsWerePressed()) {
            return
        }

        // Set the loginPressed to true
        loginPressed = true;

        // Setup the intent and go to the next activity
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    fun setupPressed(view: View){
        // Go to the setup my first device page

        // Check if the login/setup button has already been pressed
        if (checkIfButtonsWerePressed()) {
            return
        }

        // Set the setupPressed to true
        setupPressed = true

        // Setup the intent and go to the next activity
        val setupIntent = Intent(this, SetupAccountActivity::class.java)
        startActivity(setupIntent)
    }

    // Checks if either of the buttons have been pressed
    fun checkIfButtonsWerePressed(): Boolean{

        // If they have return true
        if(loginPressed || setupPressed) {
            return true;
        }

        // Else return false
        return false
    }
}