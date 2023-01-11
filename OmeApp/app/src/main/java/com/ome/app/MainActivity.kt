package com.ome.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ome.Ome.R
import com.ome.app.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isSplashScreenLoading.value
            }
        }
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initFragment()
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
        viewModel._isSplashScreenLoading.value = false

    }

    private fun onNavGraphInited() {
        lifecycle.coroutineScope.launch {
            val fragment = supportFragmentManager.findFragmentById(R.id.navHost)

            fragment?.lifecycle?.whenStarted {
                fragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
                    viewModel.currentDestination.value = destination
                }
            }
        }
    }


}
