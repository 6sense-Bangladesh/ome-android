package com.example.inirv.Login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.R

class LoginActivity : AppCompatActivity(){

    // Variables
    val emailET = findViewById<EditText>(R.id.loginEmailEditText)
    val passwordET = findViewById<EditText>(R.id.loginPasswordEditText)
    var confirmPressed: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_login)
    }

    override fun onResume() {
        super.onResume()

        // Reset the confirm button check
        confirmPressed = false
    }

    // Action for the confirm button
    fun confirmPressed(view: View){

        // Check if the confirm button was pressed
        if (confirmPressed) {
            return
        }

        // Set the confirm button to true
        confirmPressed = true;
    }

}