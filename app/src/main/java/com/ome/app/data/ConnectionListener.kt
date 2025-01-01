package com.ome.app.data

import android.content.Context
import android.net.*
import android.os.Build
import androidx.core.content.ContextCompat
import com.ome.app.utils.log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.Duration.Companion.seconds

interface ConnectionListener {

    var isConnected: Boolean
    var shouldReactOnChanges: Boolean
    val connectionStatusFlow: StateFlow<State>
    var failureNavigationFlow: MutableSharedFlow<Throwable>

    fun registerListener()
    fun unregisterListener()
    fun setDismissedStatus()

    enum class State {
        Default, HasConnection, NoConnection, Dismissed
    }
}

class ConnectionListenerImpl(
    context: Context
) : ConnectionListener {

    override var isConnected: Boolean = isNetworkAvailable()
    override var shouldReactOnChanges: Boolean = true
        set(value) {
            field = value
            if (value && connectionStatusFlow.value != ConnectionListener.State.Default) {
                connectionStatusFlow.value.let { oldState ->
                    connectionStatusFlow.value = ConnectionListener.State.Default
                    connectionStatusFlow.value = oldState
                }
            }
        }

    override var failureNavigationFlow: MutableSharedFlow<Throwable> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val connectionStatusFlow =
        MutableStateFlow(ConnectionListener.State.Default)

    private val connectivityManager =
        ContextCompat.getSystemService(context.applicationContext, ConnectivityManager::class.java)

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        .build()

    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                "Network lost".log("ConnectionListener")
                updateConnectionStatus()
            }

            override fun onAvailable(network: Network) {
                "Network available".log("ConnectionListener")
                updateConnectionStatus()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                "Network capabilities changed".log("ConnectionListener")
                updateConnectionStatus()
            }
        }
    }

    override fun registerListener() {
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        updateConnectionStatus()
    }

    override fun unregisterListener() {
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (ignore: IllegalArgumentException) {
            // Ignore: NetworkCallback was not registered
            "ConnectionListener unregisterListener exception: $ignore".log("ConnectionListener")
        }

        connectionStatusFlow.value = ConnectionListener.State.Default
    }

    override fun setDismissedStatus() {
        connectionStatusFlow.value = ConnectionListener.State.Dismissed
    }


    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager?.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            val nwInfo = connectivityManager?.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private suspend fun hasInternetAccess(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://www.google.com")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 2000
                connection.connect()
                connection.responseCode == 200
            }
        } catch (e: IOException) {
            false
        }
    }

    private var statusJob : Job? = null
    private val statusScope = CoroutineScope(Dispatchers.IO)

    private fun updateConnectionStatus() {
        statusJob?.cancel()
        statusJob = statusScope.launch {
            val networkAvailable = isNetworkAvailable()
            var retryCount = 0
            isConnected = if (networkAvailable) hasInternetAccess() else false

            while (!isConnected && retryCount < 6) {
                retryCount++
                delay(1.seconds * retryCount)
                isConnected = hasInternetAccess()
                if (isConnected) break
            }

            connectionStatusFlow.value = if (isConnected) {
                ConnectionListener.State.HasConnection
            } else {
                ConnectionListener.State.NoConnection
            }
        }
    }
}
