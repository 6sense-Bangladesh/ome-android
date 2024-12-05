package com.ome.app.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.ome.app.R
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

typealias Rssi = Int

class WifiHandler(val context: Context) {

    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    var omeKnobSSID = ""
    var inirvKnobSSID = ""
    


    var currentSSID = ""

    fun setup(macAddr: String): Pair<String, String> {
        omeKnobSSID = "Ome_Knob_${macAddr.takeLast(4)}"
        inirvKnobSSID = "Inirv_Knob_${macAddr.takeLast(4)}"
        currentSSID = omeKnobSSID
        return omeKnobSSID to inirvKnobSSID
    }

    var onConnectionChange: ((Boolean) -> Unit) = {}
    var isConnected = false

    var omeFail = false

    companion object {
        private const val TAG_ = "WifiHandler"
        private const val PASSWORD = "password"
        private const val MIN_RSSI = -100
        private const val MAX_RSSI = -50

        val Rssi.wifiStrengthPercentage : Int
            get(){
                val boundedRssi = coerceIn(MIN_RSSI, MAX_RSSI) // Ensure the RSSI value is within the expected range
                return ((boundedRssi - MIN_RSSI) * 100 / (MAX_RSSI - MIN_RSSI))
            }
    }

//    val handler = Handler(Looper.getMainLooper())

    val networkList: MutableStateFlow<List<ScanResult>?> = MutableStateFlow(null)

    suspend fun connectToKnobHotspot(
        ssid: String = currentSSID,
        password: String = PASSWORD
    ): Pair<Boolean, String?> {
        "Connecting to $currentSSID".log(TAG_)
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) false to "Location permission not granted"
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            connectModern(ssid, password)
        else
            connectLegacy(ssid, password)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun connectModern(
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
                if(!continuation.isActive) return
                connectivityManager.bindProcessToNetwork(network)
                "Connected to $currentSSID".log(TAG_)
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
                "Connection to IoT device lost".log(TAG_)
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

        return@withContext suspendCancellableCoroutine<Pair<Boolean, String?>>{ continuation ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if(!continuation.isActive) return
                    if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                        val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                        val connectedSsid = wifiManager.connectionInfo?.ssid

                        if (networkInfo?.isConnected == true && connectedSsid == "\"$ssid\"") {
                            context.unregisterReceiver(this)
                            "Connected to $currentSSID".log(TAG_)
                            continuation.resume(true to null)
                        }
                    }
                }
            }

            context.registerReceiver(receiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))

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
        "Connection failed for $currentSSID".log(TAG_)
        if(!isActive) return
        if (currentSSID == omeKnobSSID) {
            resume(false to null)
        } else {
            resume(false to this@WifiHandler.context.getString(R.string.unable_to_join_the_network, omeKnobSSID, inirvKnobSSID))
        }
        switchToOtherNetwork()
    }

    @Suppress("DEPRECATION")
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    fun disconnect(ssid: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.bindProcessToNetwork(null)
        } else {
            try {
                wifiManager.configuredNetworks?.forEach { network ->
                    if (network.SSID == "\"$ssid\"") {
                        wifiManager.removeNetwork(network.networkId)
                        wifiManager.saveConfiguration()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isConnectedToSSID(ssid: String): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            wifiManager.connectionInfo?.ssid == "\"$ssid\""
        } else {
            // Not supported in Android 13+ without location permissions
            throw UnsupportedOperationException("isConnectedToSSID is not supported on Android 13+")
        }
    }

    suspend fun connectToWifi(): Pair<Boolean, String?>{
        val currentWifiSSID = getCurrentWifiSsidOld()
        return withContext(Dispatchers.Main.immediate){
            "Connecting to $currentSSID".log(TAG_)
            suspendCancellableCoroutine { continuation ->
                if (currentWifiSSID == omeKnobSSID || currentWifiSSID == inirvKnobSSID) {
                    continuation.resume(true to null)
                } else {
                    tryInMain {
                        delay(1.minutes)
                        if (continuation.isActive) {
                            omeFail = false
                            "Connection failed for $currentSSID".log(TAG_)
                            continuation.resume(false to
                                    context.getString(R.string.unable_to_join_the_network, omeKnobSSID, inirvKnobSSID)
                            )
                        }
                    }
                    WifiUtils.withContext(context).connectWith(currentSSID, PASSWORD)
                        .onConnectionResult(object : ConnectionSuccessListener {
                            override fun success() {
                                "Connected to $currentSSID".log(TAG_)
                                if(!continuation.isActive) return
                                omeFail = false
                                continuation.resume(true to null)
                            }

                            override fun failed(errorCode: ConnectionErrorCode) {
                                "Connection failed for $currentWifiSSID".log(TAG_)
                                if(!continuation.isActive) return
                                if(!omeFail) {
                                    continuation.resume(false to null)
                                    omeFail = true
                                }else{
                                    omeFail = false
                                    continuation.resume(false to
                                            context.getString(R.string.unable_to_join_the_network, omeKnobSSID, inirvKnobSSID)
                                    )
                                }
                                switchToOtherNetwork()
                            }
                        })
                        .start()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedToWifiImpl23(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    @Suppress("DEPRECATION")
    private fun isConnectedToWifiOld(): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiManager.isWifiEnabled && wifiInfo.networkId != -1
    }



    private suspend fun getCurrentWifiSsid(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isConnectedToWifiImpl23())
            return null
        else if (!isConnectedToWifiOld())
            return null

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getCurrentWifiSsidImpl31().let {
                if(it == WifiManager.UNKNOWN_SSID)
                    getCurrentWifiSsidOld()
                else it
            }
        }
        else getCurrentWifiSsidOld()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun getCurrentWifiSsidImpl31() = suspendCancellableCoroutine{ continuation ->
        tryInMain {
            delay(3.seconds)
            if (continuation.isActive)
                continuation.resume(getCurrentWifiSsidOld())
        }
        val connectivityManager = context.applicationContext.getSystemService(ConnectivityManager::class.java)
        connectivityManager.activeNetwork
        val request =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                // Use wifiInfo as needed
                if(continuation.isActive)
                    continuation.resume(wifiInfo.ssid.trim('"'))
            }
        }
        connectivityManager?.requestNetwork(request, networkCallback)
        continuation.invokeOnCancellation {
            connectivityManager?.unregisterNetworkCallback(networkCallback) // Unregister the callback
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCurrentWifiSsidImpl29(): String {
        val connectivityManager = context.applicationContext.getSystemService(ConnectivityManager::class.java)
        val wifiInfo = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.transportInfo as? WifiInfo
        return if(wifiInfo?.ssid == null || wifiInfo.ssid == WifiManager.UNKNOWN_SSID)
            getCurrentWifiSsidOld()
        else wifiInfo.ssid.trim('"')
    }

    @Suppress("DEPRECATION")
    private fun getCurrentWifiSsidOld(): String {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid.trim('"')
    }


    suspend fun isConnectedToKnobHotspot(): Boolean {
        return withContext(Dispatchers.IO) {
            val currentWifiSSID = getCurrentWifiSsid()
            currentWifiSSID.log("isConnectedToKnobHotspot")
            currentWifiSSID == omeKnobSSID || currentWifiSSID == inirvKnobSSID
        }
    }

    private fun switchToOtherNetwork() {
        currentSSID = if (currentSSID == inirvKnobSSID) {
            omeKnobSSID
        } else {
            inirvKnobSSID
        }
    }

    suspend fun disconnectFromNetwork(): Boolean = withContext(Dispatchers.Main){
        suspendCoroutine { continuation ->
            WifiUtils.withContext(context)
                .disconnect(object : DisconnectionSuccessListener {
                    override fun success() {
                        continuation.resume(true)
                    }

                    override fun failed(errorCode: DisconnectionErrorCode) {
                        continuation.resume(false)
                    }
                })
        }
    }


    fun scan() {
        WifiUtils.withContext(context).scanWifi { results ->
            if (results.isEmpty()) {
                networkList.tryEmit(listOf())
                Log.i(TAG_, "SCAN RESULTS IT'S EMPTY")
            } else {
                networkList.tryEmit(results)
                Log.i(TAG_, "GOT SCAN RESULTS $results")
            }
        }.start()
    }
}
