package com.ome.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.utils.collectWithLifecycle
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isSplashScreenLoading }
        }
        enableEdgeToEdge()
        viewModel.registerConnectionListener()
        setContentView(R.layout.activity_main)
        initFragment()
        subscribeConnectionListener()
    }

    private fun initFragment() {
        viewModel.initStartDestination()
        viewModel.startDestination.collectWithLifecycle {
            initNavigationGraph(it)
        }
    }

    private fun initNavigationGraph(startDestinationId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.onboarding_nav_graph)
        graph.setStartDestination(startDestinationId)
        navController.setGraph(graph, intent.extras)
        viewModel.isSplashScreenLoading = false
    }

    private fun subscribeConnectionListener() {
        subscribe(viewModel.connectionStatusListener.connectionStatusFlow) { status ->
            if (viewModel.connectionStatusListener.shouldReactOnChanges) {
                when (status) {
                    ConnectionStatusListener.ConnectionStatusState.Default,
                    ConnectionStatusListener.ConnectionStatusState.HasConnection,
                    ConnectionStatusListener.ConnectionStatusState.Dismissed -> {
                    }

                    ConnectionStatusListener.ConnectionStatusState.NoConnection -> {
                        viewModel.isSplashScreenLoading = false
                        viewModel.startDestinationJob?.cancel()
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
                        navHostFragment.navController.navigate(R.id.actionInternetConnectionFragment)
                    }
                }
            }
        }
    }
}
