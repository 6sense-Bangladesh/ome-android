package com.ome.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()

    private val inAppUpdate = InAppUpdate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isSplashScreenLoading }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        viewModel.registerConnectionListener()
        setContentView(R.layout.activity_main)
        viewModel.initStartDestination()
        subscribeConnectionListener()
        inAppUpdate.checkForUpdate()
        subscribe(viewModel.defaultErrorLiveData){
            toast(it)
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

    private fun initFragment() {
    }

    private fun initNavigationGraph(startDestinationId: Int) {
        startDestinationId.log("initNavigationGraph")
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.main_nav_graph)
        graph.setStartDestination(startDestinationId)
        navController.setGraph(graph, intent.extras)
        if(navController.currentDestination?.id != startDestinationId){
            navController.navigate(startDestinationId, null,
                 NavOptions.Builder()
                     .apply {
                         navController.currentDestination?.id?.let {
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
    }

    private fun subscribeConnectionListener() {
        subscribe(viewModel.connectionStatusListener.connectionStatusFlow) { status ->
            if (viewModel.connectionStatusListener.shouldReactOnChanges) {
                when (status) {
                    ConnectionStatusListener.ConnectionStatusState.Default,
                    ConnectionStatusListener.ConnectionStatusState.HasConnection,
                    ConnectionStatusListener.ConnectionStatusState.Dismissed -> Unit

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
