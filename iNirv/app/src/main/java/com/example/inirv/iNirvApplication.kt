package com.example.inirv

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin

class iNirvApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
            Log.i("Tutorial", "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e("Tutorial", "Could not initialize Amplify", e)
        }
    }
}