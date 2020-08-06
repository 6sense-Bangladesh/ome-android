package com.example.inirv.Login

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inirv.R

class SetupAccountActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {

        // Run the super's onCreate
        super.onCreate(savedInstanceState, persistentState)

        // Set the layout to the setup a new account layout
        setContentView(R.layout.activity_setup_account)
    }
}