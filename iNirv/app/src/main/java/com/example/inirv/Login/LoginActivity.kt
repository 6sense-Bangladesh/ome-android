package com.example.inirv.Login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.example.inirv.R

class LoginActivity : AppCompatActivity(){

    // Variables
    var confirmPressed: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()


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

        // Set the views
        val emailET = findViewById<EditText>(R.id.loginEmailEditText)
        val passwordET = findViewById<EditText>(R.id.loginPasswordEditText)

        Amplify.Auth.signIn(emailET.text.toString(), passwordET.text.toString(),
            { result ->
                Log.i("AuthQuickstart", if (result.isSignInComplete) "Sign in succeeded" else "Sign in not complete")
                this.confirmPressed = false
            },
            { error ->
                Log.e("AuthQuickstart", error.toString())
                this.confirmPressed = false
            } )
    }

}