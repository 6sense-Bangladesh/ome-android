package com.ome.app.utils

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ome.Ome.R
import com.ome.app.data.local.ResourceProvider
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class WifiHandler(val context: Context, val resourceProvider: ResourceProvider) {

    var inirvKnobSSID = ""
    var omeKnobSSID = ""


    var currentSSID = ""

    companion object {
        const val password = "password"
    }

    val handler = Handler(Looper.getMainLooper())

    val networkList: MutableStateFlow<List<ScanResult>?> = MutableStateFlow(null)

    suspend fun connectToWifi(): Pair<Boolean, String?> = suspendCoroutine { continuation ->
        val currentWifiSSID = getCurrentWifiSsid()
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
                    password,
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

    private fun getCurrentWifiSsid(): String {
        val wifiManager = context.applicationContext.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
        wifiManager.connectionInfo
        return wifiManager.connectionInfo.ssid.replace("\"", "")
    }


    fun isConnectedToKnobHotspot(): Boolean {
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