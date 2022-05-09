package com.example.inirv.Settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.R

class SettingsActivity: AppCompatActivity(){

    // Variables
    var backButtonPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view
        setContentView(R.layout.activity_settings)
    }

    // Handles when the back button is pressed
    fun settingsBackButtonPressed(view: View){

        // Check if the button has already been pressed
        if (backButtonPressed){
            return;
        }

        // Run the back button pressed function
        onBackPressed()

        // Run the appropriate animations
        overridePendingTransition(R.anim.nav_default_pop_enter_anim,R.anim.slide_right_out )
    }
}