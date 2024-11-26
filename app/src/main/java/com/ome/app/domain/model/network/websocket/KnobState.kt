package com.ome.app.domain.model.network.websocket

import android.os.Parcelable
import com.ome.app.domain.model.state.ConnectionState
import com.ome.app.domain.model.state.MountingSurface
import kotlinx.parcelize.Parcelize

@Parcelize
data class KnobState(
    val angle: Double? = null,
    val mountingSurface: MountingSurface? = null,
    val battery: Int? = null,
    val temperature: Double? = null,
    val wifiStrengthPercentage: Int? = null,
    val knobReportedScheduleStop: Int? = null,
    val knobSetSafetyMode: Boolean? = null,
    val connectStatus: ConnectionState? = null,
    val connectIpAddr: String? = null,
    val firmwareVersion: String? = null
): Parcelable

typealias MacAddress = String

