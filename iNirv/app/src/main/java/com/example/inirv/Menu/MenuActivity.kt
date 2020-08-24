package com.example.inirv.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.inirv.R
import kotlinx.android.synthetic.main.activity_menu.view.*


class MenuActivity: AppCompatActivity() {

    // Variables
    var backButtonPressed: Boolean = false
    private var myContext: FragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the appropriate activity
        setContentView(R.layout.activity_menu)
    }

    // Handles when the back button is pressed
    fun menuBackButtonPressed(view: View){

        // Check if the button has already been pressed
        if (backButtonPressed){
            return;
        }

        // Run the back button pressed function
        onBackPressed()

        // Run the appropriate animations
        overridePendingTransition(R.anim.nav_default_pop_enter_anim,R.anim.slide_left_out )
    }
}