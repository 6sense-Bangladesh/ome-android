package com.example.inirv.Login

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.example.inirv.R

class LoginActivity: Activity() {

    // Variables
    var confirmPressed : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the correct layout for this activity
        setContentView(R.layout.activity_login)
    }

    // On click function for the confirm button
    fun confirmPressed(view: View){

        // Check if the confirm button has already been pressed
        if (confirmPressed) {
            return
        }

        confirmPressed = true;
    }
}