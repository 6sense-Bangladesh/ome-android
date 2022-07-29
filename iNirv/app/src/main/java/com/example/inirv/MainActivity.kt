package com.example.inirv

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // TODO: Remove when done testing
    companion object {
        //  You define a companion object to hold the API endpoint (URL),
        //  a search term and a concatenated string of the two.
        private const val URL = "https://api.github.com/search/repositories"
        //    private const val SEARCH = "q=super+mario+language:kotlin&sort=stars&order=desc"
        private const val SEARCH = "q=language:kotlin&sort=stars&order=desc&?per_page=50"
        private const val COMPLETE_URL = "$URL?$SEARCH"
    }
    // End of Remove

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_welcome )

        //  You execute the actual request using readText().
        val repoListJsonStr = java.net.URL(COMPLETE_URL).readText()
        Log.d("RestManagerTests", "URL: $repoListJsonStr")
    }
}