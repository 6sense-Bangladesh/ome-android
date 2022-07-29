package com.example.inirv;

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify

class iNirvApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())

            val config: AmplifyConfiguration = AmplifyConfiguration.fromConfigFile(applicationContext, R.raw.amplifyconfigurationdev)
            Amplify.configure(config, applicationContext)
            Log.v("restManager", "Initialized Amplify")
        } catch (error: AmplifyException) {

            Log.v("iNirvApplication", "Hey we found an error: ${error}")
        }

    }
}
