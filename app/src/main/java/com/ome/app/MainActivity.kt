package com.ome.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        initFragment()
        subscribeConnectionListener()
    }

    private fun initFragment() {
        viewModel.initStartDestination()
        subscribe(viewModel.startDestinationInitialized) { destinationWithBundle ->
            initNavigationGraph(destinationWithBundle.first, destinationWithBundle.second)
        }
    }

    private fun initNavigationGraph(startDestinationId: Int, startDestinationBundle: Bundle?) {
        val navHostFragment = NavHostFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navHost, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commitNow()

        val graph =
            navHostFragment.navController.navInflater.inflate(R.navigation.onboarding_nav_graph)
                .apply {
                    setStartDestination(startDestinationId)
                }


        navHostFragment.navController.setGraph(graph, startDestinationBundle)
        onNavGraphInited()
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

    private fun onNavGraphInited() {
        lifecycleScope.launch {
            val fragment = supportFragmentManager.findFragmentById(R.id.navHost)

            fragment?.lifecycle?.withStarted {
                fragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
                    viewModel.currentDestination.value = destination
                }
            }
        }
    }


}
