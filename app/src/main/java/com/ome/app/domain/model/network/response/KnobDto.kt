package com.ome.app.domain.model.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.network.websocket.mountingSurface
import com.ome.app.utils.Rssi
import com.ome.app.utils.WifiHandler.Companion.wifiStrengthPercentage
import kotlinx.parcelize.Parcelize

@Parcelize
data class KnobDto(
    @SerializedName("angle") val angle: Int = 0,
    @SerializedName("battery") val battery: Int = 0,
    @SerializedName("batteryVolts") val batteryVolts: Double = 0.0,
    @SerializedName("calibrated") val calibrated: Boolean? = null,
    @SerializedName("calibration") val calibration: CalibrationDto = CalibrationDto(),
    @SerializedName("connectStatus") val connectStatus: String = "",
    @SerializedName("firmwareVersion") val firmwareVersion: String = "",
    @SerializedName("gasOrElectric") val gasOrElectric: String = "",
    @SerializedName("ipAddress") val ipAddress: String = "",
    @SerializedName("lastScheduleCommand") val lastScheduleCommand: String = "",
    @SerializedName("macAddr") val macAddr: String = "",
    @SerializedName("mountingSurface") val mountingSurface: String = "",
    @SerializedName("rssi") val rssi: Rssi = 0,
    @SerializedName("safetyLock") val safetyLock: Boolean = false,
    @SerializedName("scheduleFinishTime") val scheduleFinishTime: Int = 0,
    @SerializedName("schedulePauseRemainingTime") val schedulePauseRemainingTime: Int = 0,
    @SerializedName("scheduleStartTime") val scheduleStartTime: Int = 0,
    @SerializedName("stoveId") val stoveId: String = "",
    @SerializedName("stovePosition") val stovePosition: Int = 0,
    @SerializedName("temperature") val temperature: Double = 0.0,
    @SerializedName("updated") val updated: String = "",
    @SerializedName("userId") val userId: String = ""
) : Parcelable {

    @Parcelize
    data class CalibrationDto(
        @SerializedName("offAngle") val offAngle: Int = 0,
        @SerializedName("rotationDir") val rotationDir: Int = 0,
        @SerializedName("zones") val zones: List<ZoneDto> = emptyList()
    ) : Parcelable {
        @Parcelize
        data class ZoneDto(
            @SerializedName("highAngle") val highAngle: Int = 0,
            @SerializedName("lowAngle") val lowAngle: Int = 0,
            @SerializedName("mediumAngle") val mediumAngle: Int = 0,
            @SerializedName("zoneName") val zoneName: String = "",
            @SerializedName("zoneNumber") val zoneNumber: Int = 0
        ) : Parcelable
        fun toCalibration() =
            Calibration(
                offAngle = offAngle,
                rotation = rotationDir.rotation,
                rotationClockWise = when (rotationDir) {
                    -1 -> false
                    else -> true
                },
                zones1 = zones.find { it.zoneNumber == 1 },
                zones2 = zones.find { it.zoneNumber == 2 }
            )
    }
}

val KnobDto.asKnobState
    get() = KnobState(
        angle = angle.toDouble(),
        battery = battery,
        mountingSurface = mountingSurface.mountingSurface,
        temperature = temperature,
        wifiStrengthPercentage = rssi.wifiStrengthPercentage,
        knobReportedScheduleStop = -1,
        connectStatus = connectStatus.connectionState,
        connectIpAddr = ipAddress,
        firmwareVersion = firmwareVersion,
    )

@Parcelize
data class Calibration(
    val offAngle: Int,
    val rotation: Rotation,
    val rotationClockWise: Boolean,
    val zones1: KnobDto.CalibrationDto.ZoneDto?,
    val zones2: KnobDto.CalibrationDto.ZoneDto?
): Parcelable{
    enum class Rotation{
        CLOCKWISE,
        COUNTER_CLOCKWISE,
        DUAL
    }
}

val Int?.rotation
    get() = when(this){
        1 -> Calibration.Rotation.CLOCKWISE
        -1 -> Calibration.Rotation.COUNTER_CLOCKWISE
        2 -> Calibration.Rotation.DUAL
        else -> Calibration.Rotation.CLOCKWISE
    }

enum class ConnectionState(val type: String){
    Online("online"),
    Offline("offline"),
    Charging("charging")
}

val String?.connectionState : ConnectionState
    get() = ConnectionState.entries.find { it.type == this } ?: ConnectionState.Offline
