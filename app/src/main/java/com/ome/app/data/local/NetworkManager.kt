package com.ome.app.data.local

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.ome.app.R
import com.ome.app.utils.log
import kotlinx.coroutines.*
import kotlin.coroutines.resume


class NetworkManager(val context: Context) {

    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var omeKnobSSID = ""
    private var inirvKnobSSID = ""
    var currentSSID = ""

    var onConnectionChange: ((Boolean) -> Unit) = {}
    var isConnected = false

    companion object {
        private const val TAG = "NetworkManager"
        private const val PASSWORD = "password"
        private const val MIN_RSSI = -100
        private const val MAX_RSSI = -50

        val Rssi.wifiStrengthPercentage : Int
            get(){
                val boundedRssi = coerceIn(MIN_RSSI, MAX_RSSI) // Ensure the RSSI value is within the expected range
                return ((boundedRssi - MIN_RSSI) * 100 / (MAX_RSSI - MIN_RSSI))
            }
    }

    fun setup(macAddress: String): Pair<String, String> {
        omeKnobSSID = "Ome_Knob_${macAddress.takeLast(4)}"
        inirvKnobSSID = "Inirv_Knob_${macAddress.takeLast(4)}"
        currentSSID = omeKnobSSID
        return omeKnobSSID to inirvKnobSSID
    }


    suspend fun connectToKnobHotspot(
        ssid: String = currentSSID,
        password: String = PASSWORD
    ): Pair<Boolean, String?> {
        "Connecting to $currentSSID".log(TAG)
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) false to "Location permission not granted"
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            connectModernNetworkSpecifierApi(ssid, password)
        else
            connectLegacy(ssid, password)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun connectModernNetworkSpecifierApi(
        ssid: String,
        password: String
    ): Pair<Boolean, String?> = suspendCancellableCoroutine { continuation ->
        val specifier = WifiNetworkSpecifier.Builder().apply {
            setSsid(ssid)
            setWpa2Passphrase(password)
        }.build()

        val request = NetworkRequest.Builder().apply {
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            setNetworkSpecifier(specifier)
        }.build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!continuation.isActive) return
                connectivityManager.bindProcessToNetwork(network)
                "Connected to $currentSSID".log(TAG)
                continuation.resume(true to null)
                isConnected = true
                onConnectionChange(true)
            }

            override fun onUnavailable() {
                continuation.handleHotspotConnectionFailed()
                isConnected = false
                onConnectionChange(false)
            }

            override fun onLost(network: Network) {
                "Connection to IoT device lost".log(TAG)
                connectivityManager.bindProcessToNetwork(null)
                isConnected = false
                onConnectionChange(false)
            }
        }

        connectivityManager.requestNetwork(request, networkCallback)

        continuation.invokeOnCancellation {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            connectivityManager.bindProcessToNetwork(null)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)

    private suspend fun connectModernSuggestionApi(): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        val suggestionOme = WifiNetworkSuggestion.Builder()
            .setSsid(omeKnobSSID)
            .setWpa2Passphrase(PASSWORD)
            .setIsAppInteractionRequired(true) // Ensures a prompt for user approval
            .build()
        val suggestionInirv = WifiNetworkSuggestion.Builder()
            .setSsid(inirvKnobSSID)
            .setWpa2Passphrase(PASSWORD)
            .setIsAppInteractionRequired(true) // Ensures a prompt for user approval
            .build()

        val suggestionsList = listOf(suggestionOme, suggestionInirv)

        // Add the suggestions to the Wi-Fi manager
        val result = wifiManager.addNetworkSuggestions(suggestionsList)
        if (result != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            return@withContext false to "Failed to add network suggestion: $result"
        }

        // Wait for the device to connect to the network
        suspendCancellableCoroutine<Pair<Boolean, String?>> { continuation ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (!continuation.isActive) return
                    if (intent.action == WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION) {
                        "Connected to".log(TAG)
                        context.unregisterReceiver(this)
                        continuation.resume(true to null)
                    }
                }
            }

            context.registerReceiver(
                receiver,
                IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)
            )

            continuation.invokeOnCancellation {
                runCatching {
                    context.unregisterReceiver(receiver)
                }
            }
            // Timeout for failure detection
            CoroutineScope(coroutineContext).launch {
                delay(10000) // Wait for 10 seconds
                if (continuation.isActive) {
                    context.unregisterReceiver(receiver)
                    continuation.resume(false to context.getString(R.string.unable_to_connect))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    private suspend fun connectLegacy(
        ssid: String,
        password: String
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            delay(1000)
        }

        val wifiConfig = WifiConfiguration().apply {
            SSID = "\"$ssid\""
            preSharedKey = "\"$password\""
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        }
        wifiManager.configuredNetworks?.forEach { network ->
            if (network.SSID == "\"$ssid\"") {
                wifiManager.removeNetwork(network.networkId)
            }
        }

        val networkId = wifiManager.addNetwork(wifiConfig)
        if (networkId == -1) {
            return@withContext false to "Failed to add network configuration"
        }

        return@withContext suspendCancellableCoroutine<Pair<Boolean, String?>> { continuation ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (!continuation.isActive) return
                    if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                        val networkInfo =
                            intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                        val connectedSsid = wifiManager.connectionInfo?.ssid

                        if (networkInfo?.isConnected == true && connectedSsid == "\"$ssid\"") {
                            context.unregisterReceiver(this)
                            "Connected to $currentSSID".log(TAG)
                            continuation.resume(true to null)
                        }
                    }
                }
            }

            context.registerReceiver(
                receiver,
                IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            )

            val enabled = wifiManager.enableNetwork(networkId, true)
            val reconnected = wifiManager.reconnect()

            if (!enabled || !reconnected) {
                context.unregisterReceiver(receiver)
                continuation.handleHotspotConnectionFailed()
            }

            continuation.invokeOnCancellation {
                runCatching {
                    context.unregisterReceiver(receiver)
                }
            }
        }
    }

    private fun CancellableContinuation<Pair<Boolean, String?>>.handleHotspotConnectionFailed() {
        "Connection failed for $currentSSID".log(TAG)
        if(!isActive) return
        if (currentSSID == omeKnobSSID) {
            resume(false to null)
        } else {
            resume(false to this@NetworkManager.context.getString(R.string.unable_to_connect))
        }
        switchToOtherNetwork()
    }

    @Suppress("DEPRECATION")
    fun disconnectFromKnobHotspot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.bindProcessToNetwork(null)
        }
        else if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            try {
                wifiManager.configuredNetworks?.forEach { network ->
                    if (network.SSID == "\"$omeKnobSSID\"" || network.SSID == "\"$inirvKnobSSID\"") {
                        wifiManager.removeNetwork(network.networkId)
                        wifiManager.saveConfiguration()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun switchToOtherNetwork() {
        currentSSID = if (currentSSID == inirvKnobSSID) {
            omeKnobSSID
        } else {
            inirvKnobSSID
        }
    }

}

typealias Rssi = Int
