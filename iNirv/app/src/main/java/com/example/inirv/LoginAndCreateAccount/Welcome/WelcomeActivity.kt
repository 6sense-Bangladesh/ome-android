package com.example.inirv.LoginAndCreateAccount.Welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.Interfaces.Navigator
import com.example.inirv.LoginAndCreateAccount.LoginActivity
import com.example.inirv.LoginAndCreateAccount.SetupAccountActivity
import com.example.inirv.R

class WelcomeActivity(
    var navigator: Navigator
) : AppCompatActivity() {

    // Variables
    var loginPressed: Boolean = false
    var setupAccountPressed: Boolean = false

    // On Create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup the layout for this activity
        setContentView(R.layout.fragment_welcome)

        // Setup the navigator with this activity
        navigator.activity = this

        // Setup the coordinator and fragment activity
        val coordinator: WelcomeCoordinator = WelcomeCoordinator(navigator)
        val welcomeViewModel = WelcomeViewModel(onFinished = coordinator::coordinatorInteractorFinished)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Avoid memory leaks
        navigator.activity = null
    }

    // on resume
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

    // Disable back button functionality
    override fun onBackPressed() {}
}