package com.ome.app.domain.model.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KnobDto(
    val angle: Int,
    val battery: Int,
    val batteryVolts: Double,
    val calibrated: Boolean,
    val calibration: com.ome.app.domain.model.network.response.KnobDto.CalibrationDto,
    val connectStatus: String,
    val firmwareVersion: String,
    val gasOrElectric: String,
    val ipAddress: String,
    val lastScheduleCommand: String,
    val macAddr: String,
    val mountingSurface: String,
    val rssi: Int,
    val safetyLock: Boolean,
    val scheduleFinishTime: Int,
    val schedulePauseRemainingTime: Int,
    val scheduleStartTime: Int,
    val stoveId: String,
    val stovePosition: Int,
    val temperature: Double,
    val updated: String,
    val userId: String
): Parcelable{
    @Parcelize
    data class CalibrationDto(
        val offAngle: Int,
        val rotationDir: Int,
        val zones: List<com.ome.app.domain.model.network.response.KnobDto.CalibrationDto.ZoneDto>
    ): Parcelable{
        @Parcelize
        data class ZoneDto(
            val highAngle: Int,
            val lowAngle: Int,
            val mediumAngle: Int,
            val zoneName: String,
            val zoneNumber: Int
        ): Parcelable
        fun toCalibration() =
            _root_ide_package_.com.ome.app.domain.model.network.response.Calibration(
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
    val rotation: com.ome.app.domain.model.network.response.Calibration.Rotation,
    val rotationClockWise: Boolean,
    val zones1: com.ome.app.domain.model.network.response.KnobDto.CalibrationDto.ZoneDto?,
    val zones2: _root_ide_package_.com.ome.app.domain.model.network.response.KnobDto.CalibrationDto.ZoneDto?
): Parcelable{
    enum class Rotation{
        CLOCKWISE,
        COUNTER_CLOCKWISE,
        DUAL
    }
}

val Int?.rotation
    get() = when(this){
        1 -> _root_ide_package_.com.ome.app.domain.model.network.response.Calibration.Rotation.CLOCKWISE
        -1 -> _root_ide_package_.com.ome.app.domain.model.network.response.Calibration.Rotation.COUNTER_CLOCKWISE
        2 -> _root_ide_package_.com.ome.app.domain.model.network.response.Calibration.Rotation.DUAL
        else -> _root_ide_package_.com.ome.app.domain.model.network.response.Calibration.Rotation.CLOCKWISE
    }
