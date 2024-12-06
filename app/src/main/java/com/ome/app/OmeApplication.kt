package com.ome.app

import android.app.Application
import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.kotlin.core.Amplify
import com.chesire.lifecyklelog.LifecykleLog
import com.ome.app.utils.log
import dagger.hilt.android.HiltAndroidApp
import java.lang.Exception

@HiltAndroidApp
class OmeApplication : SingletonImageLoader.Factory, Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            LifecykleLog.initialize(this)
            LifecykleLog.requireAnnotation = false
        }
        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            val configJSON = if (BuildConfig.IS_INTERNAL_TESTING) {
                R.raw.amplifyconfigurationdev
            } else {
                R.raw.amplifyconfigurationprod
            }
            AWSMobileClient.getInstance().initialize(this, AWSConfiguration(applicationContext, configJSON,"us-east-2"),object : Callback<UserStateDetails>{
                override fun onResult(result: UserStateDetails?) {
                    log("result: $result")
                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }
            })
            val config: AmplifyConfiguration = AmplifyConfiguration.fromConfigFile(
                applicationContext, configJSON)
            Amplify.configure(config, applicationContext)
            Log.i("restManager", "Initialized Amplify")
        } catch (error: AmplifyException) {

            Log.i("iNirvApplication", "Hey we found an error: $error")
        }

    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(500)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context,0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .components {
//                add(SvgDecoder.Factory())
            }
            .build()
    }
}
