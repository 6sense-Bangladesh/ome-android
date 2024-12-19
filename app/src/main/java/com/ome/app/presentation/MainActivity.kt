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
import com.ome.app.MainNavGraphDirections
import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
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
        viewModel.socketError.collectWithLifecycle {
            runCatching {
                navController?.popBackStack(R.id.connectToWifiFragment, false)
                toast(getString(R.string.something_went_wrong_when_setting_the_knob))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
        viewModel.startDestination.collectWithLifecycle {
            it.log("startDestination")
            initNavigationGraph(it)
        }
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
//         if(tryGet { navController.graph } == null) {
//            val graph = inflater.inflate(R.navigation.main_nav_graph)
//            graph.setStartDestination(startDestinationId)
//            navController.setGraph(graph, intent.extras)
//         }else{
//             navController.navigate(startDestinationId, null,
//                 NavOptions.Builder().setPopUpTo(startDestinationId, false).build()
//             )
//         }
        viewModel.isSplashScreenLoading = false
        if(startDestinationId != R.id.noInternetConnectionFragment)
            viewModel.startDestinationInitialized = true
    }

    private fun subscribeConnectionListener() {
        subscribe(viewModel.connectionStatusListener.connectionStatusFlow) { status ->
            if (viewModel.connectionStatusListener.shouldReactOnChanges) {
                when (status) {
                    ConnectionStatusListener.ConnectionStatusState.Default,
                    ConnectionStatusListener.ConnectionStatusState.Dismissed -> Unit

                    ConnectionStatusListener.ConnectionStatusState.HasConnection ->{
                        if(!viewModel.isSplashScreenLoading)
                            viewModel.connectToSocket()
                    }

                    ConnectionStatusListener.ConnectionStatusState.NoConnection -> {
                        viewModel.startDestinationJob?.cancel()
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
                        if(viewModel.isSplashScreenLoading){
                            initNavigationGraph(R.id.noInternetConnectionFragment)
                        }else{
                            runCatching {
                                navHostFragment.navController.navigate(
                                    MainNavGraphDirections.actionInternetConnectionFragment(false)
                                )
                            }
                        }
                        viewModel.isSplashScreenLoading = false
                    }
                }
            }
        }
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        savedInstanceState.getParcelable<Parcelable>("android:support:fragments")?.let {
//
//        }
//        val ss = savedInstanceState as KnobView.SavedState
//        super.onRestoreInstanceState(ss.superState)
//        for (i in 0 until childCount) {
//            getChildAt(i).restoreHierarchyState(ss.childrenStates)
//        }
//    }
}
