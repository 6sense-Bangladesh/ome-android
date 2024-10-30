package com.ome.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias Rssi = Int

class WifiHandler(val context: Context, val resourceProvider: ResourceProvider) {

    var inirvKnobSSID = ""
    var omeKnobSSID = ""


    var currentSSID = ""

    companion object {
        private const val PASSWORD = "password"
        private const val MIN_RSSI = -100
        private const val MAX_RSSI = -50

        val Rssi.signalStrengthPercentage : Int
            get(){
                val boundedRssi = coerceIn(MIN_RSSI, MAX_RSSI) // Ensure the RSSI value is within the expected range
                return ((boundedRssi - MIN_RSSI) * 100 / (MAX_RSSI - MIN_RSSI))
            }
    }

    val handler = Handler(Looper.getMainLooper())

    val networkList: MutableStateFlow<List<ScanResult>?> = MutableStateFlow(null)

    suspend fun connectToWifi(): Pair<Boolean, String?>{
        val currentWifiSSID = getCurrentWifiSsid()
        return suspendCoroutine { continuation ->
            if (currentWifiSSID == inirvKnobSSID || currentWifiSSID == omeKnobSSID) {
                continuation.resume(true to null)
            } else {
                handler.postDelayed({
                    continuation.resume(
                        false to resourceProvider.getString(
                            R.string.unable_to_join_the_network,
                            currentSSID
                        )
                    )
                }, 15000)
                WifiUtils.withContext(context)
                    .connectWith(
                        currentSSID,
                        PASSWORD,
                    )
                    .onConnectionResult(object : ConnectionSuccessListener {
                        override fun success() {
                            handler.removeCallbacksAndMessages(null)
                            continuation.resume(true to null)
                        }

                        override fun failed(errorCode: ConnectionErrorCode) {
                            handler.removeCallbacksAndMessages(null)
                            continuation.resume(
                                false to resourceProvider.getString(
                                    R.string.unable_to_join_the_network,
                                    currentSSID
                                )
                            )
                            switchToOtherNetwork()
                        }
                    })
                    .start()
                // }

            }
        }
    }

    private suspend fun getCurrentWifiSsid(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getCurrentWifiSsidNew()
        else
            getCurrentWifiSsidOld()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun getCurrentWifiSsidNew() = suspendCoroutine{ continuation->
        val connectivityManager = context.applicationContext.getSystemService(ConnectivityManager::class.java)
        val request =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                // Use wifiInfo as needed
                continuation.resume(wifiInfo.ssid.replace("\"", ""))
            }
        }
        connectivityManager?.requestNetwork(request, networkCallback)
    }

    @Suppress("DEPRECATION")
    private fun getCurrentWifiSsidOld(): String {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid.replace("\"", "")
    }


    suspend fun isConnectedToKnobHotspot(): Boolean {
        val currentWifiSSID = getCurrentWifiSsid()
        return currentWifiSSID == inirvKnobSSID || currentWifiSSID == omeKnobSSID
    }

    private fun switchToOtherNetwork() {
        currentSSID = if (currentSSID == inirvKnobSSID) {
            omeKnobSSID
        } else {
            inirvKnobSSID
        }
    }

    suspend fun disconnectFromNetwork(): Boolean = suspendCoroutine { continuation ->
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


    fun scan() {
        WifiUtils.withContext(context).scanWifi { results ->
            if (results.isEmpty()) {
                networkList.tryEmit(listOf())
                Log.i(TAG, "SCAN RESULTS IT'S EMPTY")
            } else {
                networkList.tryEmit(results)
                Log.i(TAG, "GOT SCAN RESULTS $results")
            }
        }.start()
    }

    fun setup(macAddr: String) {
        inirvKnobSSID = "Inirv_Knob_${macAddr.takeLast(4)}"
        omeKnobSSID = "Ome_Knob_${macAddr.takeLast(4)}"
        currentSSID = inirvKnobSSID
    }
}
