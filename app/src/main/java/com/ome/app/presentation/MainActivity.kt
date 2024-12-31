package com.ome.app.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.ome.app.BuildConfig
import com.ome.app.R
import com.ome.app.data.ConnectionListener
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()

    private val inAppUpdate = InAppUpdate(this)
    private var navController : NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isSplashScreenLoading }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        if (BuildConfig.IS_INTERNAL_TESTING){ Firebase.messaging.subscribeToTopic("test") }
        dynamicRotation()
        viewModel.registerConnectionListener()
        setContentView(R.layout.activity_main)
        viewModel.initStartDestination()
        subscribeConnectionListener()
        inAppUpdate.checkForUpdate()
        subscribe(viewModel.defaultErrorLiveData){
            toast(it)
        }
        viewModel.socketManager.socketError.collectWithLifecycle {
            runCatching {
                navController?.popBackStack(R.id.connectToWifiFragment, false)
                toast(getString(R.string.something_went_wrong_when_setting_the_knob))
            }
        }
        viewModel.startDestination.collectWithLifecycle {
            it.log("startDestination")
            initNavigationGraph(it)
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    @SuppressLint("RestrictedApi")
    private fun initNavigationGraph(startDestinationId: Int) {
        startDestinationId.log("initNavigationGraph")
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController
        this@MainActivity.navController = navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.main_nav_graph)
        graph.setStartDestination(startDestinationId)
        navController.setGraph(graph, intent.extras)
        if(navController.currentDestination?.id != startDestinationId && !viewModel.startDestinationInitialized){
            navController.navigate(startDestinationId, null,
                 NavOptions.Builder().apply {
                     navController.currentBackStack.value.firstOrNull()?.destination?.id?.let {
                         setPopUpTo(it, true)
                     }
                 }.build()
            )
        }
        viewModel.isSplashScreenLoading = false
        if(startDestinationId != R.id.noInternetConnectionFragment)
            viewModel.startDestinationInitialized = true
    }

    private fun subscribeConnectionListener() {
        val networkSnackBar = crateTopSnackBar(getString(R.string.no_internet_connection))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        viewModel.connectionListener.connectionStatusFlow.collectWithLifecycle { status ->
            if (viewModel.connectionListener.shouldReactOnChanges) {
                when (status) {
                    ConnectionListener.State.Default,
                    ConnectionListener.State.Dismissed -> {
                        if (networkSnackBar.isShown)
                            networkSnackBar.dismiss()
                    }

                    ConnectionListener.State.HasConnection ->{
                        if(!viewModel.isSplashScreenLoading)
                            viewModel.connectToSocket()
                        if (networkSnackBar.isShown)
                            networkSnackBar.dismiss()
                    }

                    ConnectionListener.State.NoConnection -> {
                        viewModel.startDestinationJob?.cancel()
                        if(viewModel.isSplashScreenLoading){
                            initNavigationGraph(R.id.noInternetConnectionFragment)
                        }else if(navHostFragment.navController.currentDestination?.id != R.id.noInternetConnectionFragment){
                            toast(getString(R.string.no_internet_connection))
//                            if (!networkSnackBar.isShown)
                                networkSnackBar.show()
                        }
                        viewModel.isSplashScreenLoading = false
                    }
                }
            }
        }
    }

}
