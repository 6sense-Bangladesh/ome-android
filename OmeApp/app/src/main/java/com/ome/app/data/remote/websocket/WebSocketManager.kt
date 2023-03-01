package com.ome.app.data.remote.websocket

import android.os.Handler
import android.os.Looper
import com.ome.Ome.BuildConfig
import com.ome.app.model.network.websocket.*
import com.ome.app.utils.FlowStreamAdapter
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class WebSocketManager {
    private var scarlet: Scarlet? = null


    val knobAngleFlow: MutableStateFlow<KnobAngle?> = MutableStateFlow(null)
    val knobBatteryFlow: MutableStateFlow<KnobBattery?> = MutableStateFlow(null)
    val knobConnectStatusFlow: MutableStateFlow<KnobConnectStatus?> = MutableStateFlow(null)
    val knobConnectIpAddrFlow: MutableStateFlow<KnobConnectIpAddr?> = MutableStateFlow(null)
    val knobFirmwareVersionFlow: MutableStateFlow<KnobFirmwareVersion?> = MutableStateFlow(null)
    val knobMountingSurfaceFlow: MutableStateFlow<KnobMountingSurface?> = MutableStateFlow(null)
    val knobReportedScheduleStopFlow: MutableStateFlow<KnobReportedScheduleStop?> =
        MutableStateFlow(null)
    val knobRssiFlow: MutableStateFlow<KnobRssi?> = MutableStateFlow(null)
    val knobTemperatureFlow: MutableStateFlow<KnobTemperature?> = MutableStateFlow(null)

    suspend fun initWebSocket(macAddrs: List<String>, userId: String) {
        val url = "${BuildConfig.BASE_WEB_SOCKET_URL}?knobMacAddr=${
            macAddrs.joinToString(
                separator = ","
            ) { it }
        }&inirvUid=$userId"



        scarlet = Scarlet.Builder()
            .webSocketFactory(
                createHttpClient().newWebSocketFactory(url)
            )
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
            .build()
        subscribe()
    }


    private fun createHttpClient(
    ): OkHttpClient {
        val client: OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(Interceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .addHeader("x-inirv-vsn", "6").build()
                    chain.proceed(request)
                })
                .build()

        return client
    }

    suspend fun subscribe() {
        try{
            getKnobService()?.knobMessageEvent()?.collect {
                when (it.name) {
                    "angle" -> {
                        knobAngleFlow.tryEmit(
                            KnobAngle(
                                it.macAddr,
                                it.name,
                                it.value as Double
                            )
                        )
                    }
                    "mountingSurface" -> {
                        knobMountingSurfaceFlow.emit(
                            KnobMountingSurface(
                                it.macAddr,
                                it.name,
                                it.value as String
                            )
                        )
                    }
                    "battery" -> {
                        knobBatteryFlow.emit(KnobBattery(it.macAddr, it.name, it.value as Int))
                    }
                    "temperature" -> {
                        knobTemperatureFlow.emit(
                            KnobTemperature(
                                it.macAddr,
                                it.name,
                                it.value as Double
                            )
                        )
                    }
                    "rssi" -> {
                        knobRssiFlow.emit(KnobRssi(it.macAddr, it.name, it.value as Int))
                    }
                    "connectStatus" -> {
                        knobConnectStatusFlow.emit(
                            KnobConnectStatus(
                                it.macAddr,
                                it.name,
                                it.value as String
                            )
                        )
                    }
                    "connectIpAddr" -> {
                        knobConnectIpAddrFlow.emit(
                            KnobConnectIpAddr(
                                it.macAddr,
                                it.name,
                                it.value as String
                            )
                        )
                    }
                    "firmwareVersion" -> {
                        knobFirmwareVersionFlow.emit(
                            KnobFirmwareVersion(
                                it.macAddr,
                                it.name,
                                it.value as String
                            )
                        )
                    }
                    "knobReportedScheduleStopFlow" -> {
                        knobReportedScheduleStopFlow.emit(
                            KnobReportedScheduleStop(
                                it.macAddr,
                                it.name,
                                it.value as Int
                            )
                        )
                    }

                }

            }
        }catch (ex: Exception){
            val text = ""
        }

    }

    suspend fun getKnobService(): KnobWebSocketService? {
        return scarlet?.create(KnobWebSocketService::class.java)
    }

}
