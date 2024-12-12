package com.ome.app.domain.model.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ome.app.data.local.Rssi
import com.ome.app.domain.model.network.request.SetCalibrationRequest
import com.ome.app.domain.model.network.request.Zone
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.state.*
import com.ome.app.data.local.NetworkManager.Companion.wifiStrengthPercentage
import com.ome.app.utils.isFalse
import com.ome.app.utils.orFalse
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
        ) : Parcelable{
            fun toZone() = Zone(
                highAngle = highAngle,
                lowAngle = lowAngle,
                mediumAngle = mediumAngle,
                zoneName = zoneName,
                zoneNumber = zoneNumber
            )
        }

        fun toCalibration(isCalibrated: Boolean?) =
            Calibration(
                isCalibrated = isCalibrated.orFalse(),
                offAngle = offAngle,
                rotation = rotationDir.rotation,
                rotationClockWise = when (rotationDir) {
                    -1 -> false
                    else -> true
                },
                zone1 = zones.find { it.zoneNumber == 1 },
                zone2 = zones.find { it.zoneNumber == 2 }
            )
        fun toSetCalibrationRequest() = SetCalibrationRequest(
            offAngle = offAngle,
            rotationDir = rotationDir,
            zones = zones.map { it.toZone() }
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
        knobSetSafetyMode = safetyLock,
        connectStatus = connectStatus.connectionState,
        connectIpAddr = ipAddress,
        firmwareVersion = firmwareVersion,
    )

val KnobDto.asBurnerState
    get()= buildList{
        if(calibrated.isFalse()) return@buildList
        val cal = calibration.toCalibration(calibrated)
        add(BurnerState.Off(cal.offAngle))
        if(cal.zone1 != null){
            add(BurnerState.Low(cal.zone1.lowAngle))
            add(BurnerState.Medium(cal.zone1.mediumAngle))
            add(BurnerState.High(cal.zone1.highAngle))
            if(cal.rotation != Rotation.DUAL) {
                add(BurnerState.LowMid((cal.zone1.lowAngle + cal.zone1.mediumAngle)/2))
                add(BurnerState.HighMid((cal.zone1.highAngle + cal.zone1.mediumAngle)/2))
            }
        }
        if(cal.zone2 != null){
            add(BurnerState.Low(cal.zone2.lowAngle))
            add(BurnerState.Medium(cal.zone2.mediumAngle))
//            add(BurnerState.Medium(KnobAngleManager.generateMediumAngle(cal.zone2.lowAngle, cal.zone2.highAngle)))
            add(BurnerState.High(cal.zone2.highAngle))
        }
    }


@Parcelize
data class Calibration(
    val isCalibrated: Boolean,
    val offAngle: Int,
    val rotation: Rotation,
    val rotationClockWise: Boolean,
    val zone1: KnobDto.CalibrationDto.ZoneDto?,
    val zone2: KnobDto.CalibrationDto.ZoneDto?
): Parcelable
