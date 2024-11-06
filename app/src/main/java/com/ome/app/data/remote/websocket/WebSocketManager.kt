package com.ome.app.data.remote.websocket

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ome.app.BuildConfig
import com.ome.app.domain.model.state.KnobEntity
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.asKnobState
import com.ome.app.domain.model.state.connectionState
import com.ome.app.domain.model.state.knobEntity
import com.ome.app.domain.model.state.mountingSurface
import com.ome.app.domain.model.network.websocket.*
import com.ome.app.utils.FlowStreamAdapter
import com.ome.app.utils.WifiHandler.Companion.wifiStrengthPercentage
import com.ome.app.utils.orMinusOne
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class WebSocketManager(
    private val context: Context
) {
    private var scarlet: Scarlet? = null

    val knobState= MutableStateFlow(mutableMapOf<MacAddress, KnobState>())

    val knobAngleFlow: MutableStateFlow<KnobAngle?> = MutableStateFlow(null)

    val knobBatteryFlow: MutableStateFlow<KnobBattery?> = MutableStateFlow(null)
    val knobConnectStatusFlow: MutableStateFlow<KnobConnectStatus?> = MutableStateFlow(null)
    val knobConnectIpAddrFlow: MutableStateFlow<KnobConnectIpAddr?> = MutableStateFlow(null)
    val knobFirmwareVersionFlow: MutableStateFlow<KnobFirmwareVersion?> = MutableStateFlow(null)
    val knobMountingSurfaceFlow: MutableStateFlow<KnobMountingSurface?> = MutableStateFlow(null)
    val knobReportedScheduleStopFlow: MutableStateFlow<KnobReportedScheduleStop?> = MutableStateFlow(null)
    val knobRssiFlow: MutableStateFlow<KnobRssi?> = MutableStateFlow(null)
    val knobTemperatureFlow: MutableStateFlow<KnobTemperature?> = MutableStateFlow(null)

    suspend fun initWebSocket(knobs: List<KnobDto>, userId: String) {
        val url = "${BuildConfig.BASE_WEB_SOCKET_URL}?knobMacAddr=${knobs.map { knob -> knob.macAddr }.joinToString(separator = ",") { it }}&inirvUid=$userId"
        val knobStates= knobs.associateBy { it.macAddr }.mapValues { it.value.asKnobState }
        knobState.value = knobStates.toMutableMap()

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
            getKnobService()?.knobMessageEvent()?.collect {
                Log.d("getKnobService", "subscribe: ${it.name} - ${it.name} ${it.value} ${it.macAddr}")
                val knobEntity = it.name.knobEntity
                var needRefresh = true
                when (knobEntity) {
                    KnobEntity.ANGLE -> {
                        knobAngleFlow.tryEmit(KnobAngle(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            it.value as Double
                        ))
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(angle = it.value)
                        else
                            knobState.value[it.macAddr] = KnobState(angle = it.value)
                    }
                    KnobEntity.MOUNTING_SURFACE -> {
                        knobMountingSurfaceFlow.emit(KnobMountingSurface(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            it.value as String
                        ))
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(mountingSurface = it.value.mountingSurface)
                        else
                            knobState.value[it.macAddr] = KnobState(mountingSurface = it.value.mountingSurface)
                    }
                    KnobEntity.BATTERY -> {
                        val value = it.value.toString().toIntOrNull().orMinusOne()
                        knobBatteryFlow.emit(KnobBattery(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            value
                        ))
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(battery = value)
                        else
                            knobState.value[it.macAddr] = KnobState(battery = value)
                    }
                    KnobEntity.TEMPERATURE -> {
                        knobTemperatureFlow.emit(KnobTemperature(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            it.value as Double
                        ))
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(temperature = it.value)
                        else
                            knobState.value[it.macAddr] = KnobState(temperature = it.value)
                    }
                    KnobEntity.RSSI -> {
                        knobRssiFlow.emit(KnobRssi(
                            it.macAddr.orEmpty(),
                            knobEntity.key,
                            it.value as Int
                        ))
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(wifiStrengthPercentage = it.value.wifiStrengthPercentage)
                        else
                            knobState.value[it.macAddr] = KnobState(wifiStrengthPercentage = it.value.wifiStrengthPercentage)

                    }
                    KnobEntity.CONNECT_STATUS -> {
                        knobConnectStatusFlow.emit(
                            KnobConnectStatus(
                                it.macAddr.orEmpty(),
                                knobEntity.key,
                                it.value as String
                            )
                        )
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(connectStatus = it.value.connectionState)
                        else
                            knobState.value[it.macAddr] = KnobState(connectStatus = it.value.connectionState)
                    }
                    KnobEntity.CONNECT_IP_ADD -> {
                        knobConnectIpAddrFlow.emit(
                            KnobConnectIpAddr(
                                it.macAddr.orEmpty(),
                                knobEntity.key,
                                it.value as String
                            )
                        )
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(connectIpAddr = it.value)
                        else
                            knobState.value[it.macAddr] = KnobState(connectIpAddr = it.value)
                    }
                    KnobEntity.FIRMWARE_VERSION -> {
                        knobFirmwareVersionFlow.emit(
                            KnobFirmwareVersion(
                                it.macAddr.orEmpty(),
                                knobEntity.key,
                                it.value as String
                            )
                        )
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(firmwareVersion = it.value)
                        else
                            knobState.value[it.macAddr] = KnobState(firmwareVersion = it.value)
                    }
                    KnobEntity.KNOB_REPORTED_SCHEDULE_STOP -> {
                        knobReportedScheduleStopFlow.emit(
                            KnobReportedScheduleStop(
                                it.macAddr.orEmpty(),
                                knobEntity.key,
                                it.value as Int
                            )
                        )
                        if(it.macAddr == null) return@collect
                        if(knobState.value.containsKey(it.macAddr))
                            knobState.value[it.macAddr] = knobState.value[it.macAddr]!!.copy(knobReportedScheduleStop = it.value)
                        else
                            knobState.value[it.macAddr] = KnobState(knobReportedScheduleStop = it.value)
                    }
                    KnobEntity.KNOB_POST, KnobEntity.KNOB_PATCH, KnobEntity.KNOB_SET_CALIBRATION, KnobEntity.KNOB_DELETE -> {
//                        stoveRepository.getAllKnobs()
                        needRefresh = false
                    }
                    KnobEntity.USER_POST, KnobEntity.USER_PATCH, KnobEntity.USER_DELETE -> {
//                        userRepository.getUserData()
                        needRefresh = false
                    }
                    null -> needRefresh = false
                }
                if(needRefresh)
                    knobState.value = knobState.value.toMutableMap()
            }
        } catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    private fun getKnobService(): KnobWebSocketService? {
        return scarlet?.create(KnobWebSocketService::class.java)
    }

}
