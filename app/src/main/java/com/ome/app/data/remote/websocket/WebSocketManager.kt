package com.ome.app.data.remote.websocket

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ome.app.BuildConfig
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.asKnobState
import com.ome.app.domain.model.network.websocket.KnobAngle
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.network.websocket.MacAddress
import com.ome.app.domain.model.state.KnobEntity
import com.ome.app.domain.model.state.connectionState
import com.ome.app.domain.model.state.knobEntity
import com.ome.app.domain.model.state.mountingSurface
import com.ome.app.utils.FlowStreamAdapter
import com.ome.app.utils.WifiHandler.Companion.wifiStrengthPercentage
import com.ome.app.utils.orMinusOne
import com.ome.app.utils.tryInMain
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.minutes

class WebSocketManager(
    private val context: Context
) {
    private var scarlet: Scarlet? = null

    val knobState= MutableStateFlow(mapOf<MacAddress, KnobState>())

    val knobAngleFlow: MutableStateFlow<KnobAngle?> = MutableStateFlow(null)

    var onSocketConnect: suspend (Boolean) -> Unit = {}
    var connected = false


//    private val knobBatteryFlow: MutableStateFlow<KnobBattery?> = MutableStateFlow(null)
//    private val knobConnectStatusFlow: MutableStateFlow<KnobConnectStatus?> = MutableStateFlow(null)
//    private val knobConnectIpAddrFlow: MutableStateFlow<KnobConnectIpAddr?> = MutableStateFlow(null)
//    private val knobFirmwareVersionFlow: MutableStateFlow<KnobFirmwareVersion?> = MutableStateFlow(null)
//    private val knobMountingSurfaceFlow: MutableStateFlow<KnobMountingSurface?> = MutableStateFlow(null)
//    private val knobReportedScheduleStopFlow: MutableStateFlow<KnobReportedScheduleStop?> = MutableStateFlow(null)
//    private val knobRssiFlow: MutableStateFlow<KnobRssi?> = MutableStateFlow(null)
//    private val knobTemperatureFlow: MutableStateFlow<KnobTemperature?> = MutableStateFlow(null)

    suspend fun initWebSocket(knobs: List<KnobDto>, userId: String) {
        val url = "${BuildConfig.BASE_WEB_SOCKET_URL}?knobMacAddr=${knobs.map { knob -> knob.macAddr }.joinToString(separator = ",") { it }}&inirvUid=$userId"
        val knobStates= knobs.associateBy { it.macAddr }.mapValues { it.value.asKnobState }
        knobState.value = knobStates

        scarlet = Scarlet.Builder()
            .webSocketFactory(
                createHttpClient().newWebSocketFactory(url)
            )
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
            .build()
        CoroutineScope(coroutineContext).launch {
            subscribe()
        }
//        delay(4.seconds)
    }


    private fun createHttpClient(
    ): OkHttpClient {
        val client: OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(ChuckerInterceptor(context))
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
            tryInMain {
                delay(1.2.minutes)
                onSocketConnect(connected)
            }
            getKnobService()?.knobMessageEvent()?.collect {
                connected = true
                Log.d("getKnobService", "subscribe: ${it.name} - ${it.name} ${it.value} ${it.macAddr}")
                val knobEntity = it.name.knobEntity
                var needRefresh = true
                when (knobEntity) {
                    KnobEntity.ANGLE -> {
                        knobAngleFlow.value = KnobAngle(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            it.value as Double
                        )
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            this[it.macAddr] = currentKnobState?.copy(angle = it.value) ?: KnobState(angle = it.value)
                        }.toMap()
                    }
                    KnobEntity.MOUNTING_SURFACE -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().mountingSurface
                            this[it.macAddr] = currentKnobState?.copy(mountingSurface = value) ?: KnobState(mountingSurface = value)
                        }.toMap()
                    }
                    KnobEntity.BATTERY -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().toIntOrNull().orMinusOne()
                            this[it.macAddr] = currentKnobState?.copy(battery = value) ?: KnobState(battery = value)
                        }.toMap()
                    }
                    KnobEntity.TEMPERATURE -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().toDoubleOrNull()
                            this[it.macAddr] = currentKnobState?.copy(temperature = value) ?: KnobState(temperature = value)
                        }.toMap()
                    }
                    KnobEntity.RSSI -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().toDoubleOrNull()?.toInt()?.wifiStrengthPercentage
                            this[it.macAddr] = currentKnobState?.copy(wifiStrengthPercentage = value) ?: KnobState(wifiStrengthPercentage = value)
                        }.toMap()

                    }
                    KnobEntity.CONNECT_STATUS -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().connectionState
                            this[it.macAddr] = currentKnobState?.copy(connectStatus = value) ?: KnobState(connectStatus = value)
                        }.toMap()
                    }
                    KnobEntity.CONNECT_IP_ADD -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString()
                            this[it.macAddr] = currentKnobState?.copy(connectIpAddr = value) ?: KnobState(connectIpAddr = value)
                        }.toMap()
                    }
                    KnobEntity.FIRMWARE_VERSION -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString()
                            this[it.macAddr] = currentKnobState?.copy(firmwareVersion = value) ?: KnobState(firmwareVersion = value)
                        }.toMap()
                    }
                    KnobEntity.KNOB_REPORTED_SCHEDULE_STOP -> {
                        if(it.macAddr == null) return@collect
                        knobState.value = knobState.value.toMutableMap().apply {
                            val currentKnobState = this[it.macAddr]
                            val value = it.value.toString().toIntOrNull()
                            this[it.macAddr] = currentKnobState?.copy(knobReportedScheduleStop = value) ?: KnobState(knobReportedScheduleStop = value)
                        }.toMap()
                    }
                    KnobEntity.KNOB_POST, KnobEntity.KNOB_PATCH, KnobEntity.KNOB_SET_CALIBRATION, KnobEntity.KNOB_DELETE -> {
                        needRefresh = false
                    }
                    KnobEntity.USER_POST, KnobEntity.USER_PATCH, KnobEntity.USER_DELETE -> {
                        needRefresh = false
                    }
                    null -> needRefresh = false
                }
                if(needRefresh)
                    knobState.value = knobState.value.toMutableMap()
                onSocketConnect(connected)
            }
        } catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    private fun getKnobService(): KnobWebSocketService? {
        return scarlet?.create(KnobWebSocketService::class.java)
    }

}
