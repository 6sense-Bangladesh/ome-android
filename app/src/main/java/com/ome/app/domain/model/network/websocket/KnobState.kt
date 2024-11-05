package com.ome.app.domain.model.network.websocket

import android.os.Parcelable
import com.ome.app.domain.model.network.response.ConnectionState
import kotlinx.parcelize.Parcelize

@Parcelize
data class KnobState(
    val angle: Double? = null,
    val mountingSurface: MountingSurface? = null,
    val battery: Int? = null,
    val temperature: Double? = null,
    val wifiStrengthPercentage: Int? = null,
    val knobReportedScheduleStop: Int? = null,
    val connectStatus: ConnectionState? = null,
    val connectIpAddr: String? = null,
    val firmwareVersion: String? = null
): Parcelable

typealias MacAddress = String

enum class MountingSurface(val key: String) {
    VERTICAL("vertical"),
    HORIZONTAL("horizontal")
}

val String?.mountingSurface: MountingSurface?
    get() = MountingSurface.entries.find { it.key == this }

