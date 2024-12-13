package com.ome.app.data.remote.websocket

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ome.app.BuildConfig
import com.ome.app.data.local.NetworkManager.Companion.wifiStrengthPercentage
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.asKnobState
import com.ome.app.domain.model.network.websocket.KnobAngle
import com.ome.app.domain.model.network.websocket.KnobMessageEvent
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.network.websocket.MacAddress
import com.ome.app.domain.model.state.ConnectionState
import com.ome.app.domain.model.state.KnobEntity
import com.ome.app.domain.model.state.connectionState
import com.ome.app.domain.model.state.knobEntity
import com.ome.app.domain.model.state.mountingSurface
import com.ome.app.utils.log
import com.ome.app.utils.orMinusOne
import com.ome.app.utils.tryInMain
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.serialization.gson.GsonWebsocketContentConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class WebSocketManager(private val context: Context) {
    val knobState = MutableStateFlow(mapOf<MacAddress, KnobState>())
    val knobAngleFlow: MutableStateFlow<KnobAngle?> = MutableStateFlow(null)
    var onSocketConnect: suspend (Boolean) -> Unit = {}
    private var connected = false

    companion object { private const val TAG = "KnobSocketWeb" }

    val client = HttpClient(OkHttp) {
        engine {
            config { followRedirects(true) }
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            addInterceptor(ChuckerInterceptor(context))
            preconfigured = OkHttpClient.Builder()
                .pingInterval(interval = 5000L, TimeUnit.MILLISECONDS)
                .build()
        }
        install(WebSockets) {contentConverter = GsonWebsocketContentConverter() }
        install(DefaultRequest) { header("x-inirv-vsn", "6") }
    }

    suspend fun initKnobWebSocket(knobs: List<KnobDto>, userId: String) {
        tryInMain { delay(1.2.minutes) ; onSocketConnect(connected) }
        val knobMacs = knobs.joinToString(separator = ",") { knob -> knob.macAddr }
        val knobStates = knobs.associateBy { it.macAddr }.mapValues { it.value.asKnobState }
        knobState.value = knobStates
        withContext(Dispatchers.IO) {
            runCatching {
                client.webSocket(
                    urlString = ""
                ){

                }
                client.webSocket(urlString = "${BuildConfig.BASE_WEB_SOCKET_URL}?knobMacAddr=$knobMacs&inirvUid=$userId") {
                    while (this@webSocket.isActive){
                        receiveDeserialized<KnobMessageEvent>().also {
                            "getKnobService: ${it.name} - ${it.name} ${it.value} ${it.macAddr}".log(TAG)
                            val knobEntity = it.name.knobEntity
                            var needRefresh = true
                            when (knobEntity) {
                                KnobEntity.ANGLE -> {
                                    knobAngleFlow.value = KnobAngle(
                                        it.macAddr.orEmpty(),
                                        knobEntity.key,
                                        it.value as Double
                                    )
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        this[it.macAddr] = currentKnobState?.copy(angle = it.value)
                                            ?: KnobState(angle = it.value)
                                    }.toMap()
                                }

                                KnobEntity.MOUNTING_SURFACE -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().mountingSurface
                                        this[it.macAddr] =
                                            currentKnobState?.copy(mountingSurface = value)
                                                ?: KnobState(mountingSurface = value)
                                    }.toMap()
                                }

                                KnobEntity.BATTERY -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().toIntOrNull().orMinusOne()
                                        this[it.macAddr] = currentKnobState?.copy(battery = value)
                                            ?: KnobState(battery = value)
                                    }.toMap()
                                }

                                KnobEntity.TEMPERATURE -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().toDoubleOrNull()
                                        this[it.macAddr] =
                                            currentKnobState?.copy(temperature = value) ?: KnobState(
                                                temperature = value
                                            )
                                    }.toMap()
                                }

                                KnobEntity.RSSI -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().toDoubleOrNull()
                                            ?.toInt()?.wifiStrengthPercentage
                                        this[it.macAddr] =
                                            currentKnobState?.copy(wifiStrengthPercentage = value)
                                                ?: KnobState(wifiStrengthPercentage = value)
                                    }.toMap()

                                }

                                KnobEntity.CONNECT_STATUS -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().connectionState
                                        connected = value == ConnectionState.Online
                                        onSocketConnect(connected)
                                        this[it.macAddr] =
                                            currentKnobState?.copy(connectStatus = value) ?: KnobState(
                                                connectStatus = value
                                            )
                                    }.toMap()
                                }

                                KnobEntity.CONNECT_IP_ADD -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString()
                                        this[it.macAddr] =
                                            currentKnobState?.copy(connectIpAddr = value) ?: KnobState(
                                                connectIpAddr = value
                                            )
                                    }.toMap()
                                }

                                KnobEntity.FIRMWARE_VERSION -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString()
                                        this[it.macAddr] =
                                            currentKnobState?.copy(firmwareVersion = value)
                                                ?: KnobState(firmwareVersion = value)
                                    }.toMap()
                                }

                                KnobEntity.KNOB_REPORTED_SCHEDULE_STOP -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().toIntOrNull()
                                        this[it.macAddr] =
                                            currentKnobState?.copy(knobReportedScheduleStop = value)
                                                ?: KnobState(knobReportedScheduleStop = value)
                                    }.toMap()
                                }

                                KnobEntity.KNOB_SET_SAFETY_MODE -> {
                                    if (it.macAddr == null) return@also
                                    knobState.value = knobState.value.toMutableMap().apply {
                                        val currentKnobState = this[it.macAddr]
                                        val value = it.value.toString().toBoolean()
                                        this[it.macAddr] =
                                            currentKnobState?.copy(knobSetSafetyMode = value)
                                                ?: KnobState(knobSetSafetyMode = value)
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
                            if (knobEntity != KnobEntity.CONNECT_STATUS) {
                                connected = true
                                onSocketConnect(true)
                            }
                            if (needRefresh)
                                knobState.value = knobState.value.toMutableMap()
                        }
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}