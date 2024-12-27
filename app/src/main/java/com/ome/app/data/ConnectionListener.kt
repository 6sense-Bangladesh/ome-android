package com.ome.app.data

import android.content.Context
import android.net.*
import android.os.Build
import androidx.core.content.ContextCompat
import com.ome.app.utils.log
import com.ome.app.utils.loge
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                isConnected = false
                if (connectionStatusFlow.value != ConnectionListener.State.Dismissed) {
                    connectionStatusFlow.value = ConnectionListener.State.NoConnection
                }
            }

            override fun onAvailable(network: Network) {
                log("Network available")
                isConnected = true
                connectionStatusFlow.value = ConnectionListener.State.HasConnection
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

        connectionStatusFlow.value = ConnectionListener.State.Default
    }

    override fun setDismissedStatus() {
        connectionStatusFlow.value = ConnectionListener.State.Dismissed
    }


    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager?.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
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
        connectionStatusFlow.value =(
            if (!isNetworkAvailable()) {
                ConnectionListener.State.NoConnection
            } else {
                ConnectionListener.State.HasConnection
            }
        )
    }
}
