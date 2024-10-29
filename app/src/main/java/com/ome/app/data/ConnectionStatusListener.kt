package com.ome.app.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.ContextCompat
import com.ome.app.utils.loge
import com.ome.app.utils.logi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ConnectionStatusListener {
    val connectionStatusFlow: StateFlow<ConnectionStatusState>

    var failureNavigationFlow: MutableSharedFlow<Throwable>

    fun registerListener()
    fun unregisterListener()
    fun setDismissedStatus()

    enum class ConnectionStatusState {
        Default, HasConnection, NoConnection, Dismissed
    }

    var shouldReactOnChanges: Boolean
}

class ConnectionStatusListenerImpl(
    private val context: Context
) : ConnectionStatusListener {

    override var shouldReactOnChanges: Boolean = true

    override var failureNavigationFlow: MutableSharedFlow<Throwable> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val connectionStatusFlow =
        MutableStateFlow(ConnectionStatusListener.ConnectionStatusState.Default)

    private val connectivityManager by lazy {
        ContextCompat.getSystemService(context.applicationContext, ConnectivityManager::class.java)
    }
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                if (connectionStatusFlow.value != ConnectionStatusListener.ConnectionStatusState.Dismissed) {
                    connectionStatusFlow.tryEmit(
                        ConnectionStatusListener.ConnectionStatusState.NoConnection
                    )
                }
            }

            override fun onAvailable(network: Network) {
                logi("Network available")
                connectionStatusFlow.tryEmit(
                    ConnectionStatusListener.ConnectionStatusState.HasConnection
                )
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
            // ignore: NetworkCallback was not registered
            loge("ConnectionStatusListener unregisterListener exception: $ignore")
        }

        connectionStatusFlow.tryEmit(
            ConnectionStatusListener.ConnectionStatusState.Default
        )
    }

    override fun setDismissedStatus() {
        connectionStatusFlow.tryEmit(
            ConnectionStatusListener.ConnectionStatusState.Dismissed
        )
    }


    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager?.activeNetwork ?: return false
            val actNw = connectivityManager?.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager?.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun updateConnectionStatus() {
        connectionStatusFlow.tryEmit(
            if (!isNetworkAvailable()) {
                ConnectionStatusListener.ConnectionStatusState.NoConnection
            } else {
                ConnectionStatusListener.ConnectionStatusState.HasConnection
            }
        )
    }
}
