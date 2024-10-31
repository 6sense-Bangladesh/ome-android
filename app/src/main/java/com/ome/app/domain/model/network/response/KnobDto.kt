package com.ome.app.domain.model.network.response

import android.os.Parcelable
import com.ome.app.utils.Rssi
import kotlinx.parcelize.Parcelize

@Parcelize
data class KnobDto(
    val angle: Int = 0,
    val battery: Int = 0,
    val batteryVolts: Double = 0.0,
    val calibrated: Boolean? = null,
    val calibration: CalibrationDto = CalibrationDto(),
    val connectStatus: String = "",
    val firmwareVersion: String = "",
    val gasOrElectric: String = "",
    val ipAddress: String = "",
    val lastScheduleCommand: String = "",
    val macAddr: String = "",
    val mountingSurface: String = "",
    val rssi: Rssi = 0,
    val safetyLock: Boolean = false,
    val scheduleFinishTime: Int = 0,
    val schedulePauseRemainingTime: Int = 0,
    val scheduleStartTime: Int = 0,
    val stoveId: String = "",
    val stovePosition: Int = 0,
    val temperature: Double = 0.0,
    val updated: String = "",
    val userId: String = ""
) : Parcelable {

    @Parcelize
    data class CalibrationDto(
        val offAngle: Int = 0,
        val rotationDir: Int = 0,
        val zones: List<ZoneDto> = emptyList()
    ) : Parcelable {
        @Parcelize
        data class ZoneDto(
            val highAngle: Int = 0,
            val lowAngle: Int = 0,
            val mediumAngle: Int = 0,
            val zoneName: String = "",
            val zoneNumber: Int = 0
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
