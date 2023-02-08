package com.ome.app

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify
import com.ome.Ome.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OmeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            val config: AmplifyConfiguration = AmplifyConfiguration.fromConfigFile(
                applicationContext,
                R.raw.amplifyconfigurationprod
            )
            Amplify.configure(config, applicationContext)
            Log.i("restManager", "Initialized Amplify")
        } catch (error: AmplifyException) {

            Log.i("iNirvApplication", "Hey we found an error: ${error}")
        }

    }
}
