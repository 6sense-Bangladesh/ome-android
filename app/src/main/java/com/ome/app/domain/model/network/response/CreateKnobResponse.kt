package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class CreateKnobResponse(
    @SerializedName("angle") val angle: Int,
    @SerializedName("battery") val battery: Int,
    @SerializedName("batteryVolts") val batteryVolts: Double,
    @SerializedName("calibrated") val calibrated: Boolean,
    @SerializedName("calibration") val calibration: KnobDto.CalibrationDto,
    @SerializedName("connectStatus") val connectStatus: String,
    @SerializedName("firmwareVersion") val firmwareVersion: String,
    @SerializedName("gasOrElectric") val gasOrElectric: String,
    @SerializedName("ipAddress") val ipAddress: String,
    @SerializedName("lastScheduleCommand") val lastScheduleCommand: String,
    @SerializedName("macAddr") val macAddr: String,
    @SerializedName("mountingSurface") val mountingSurface: String,
    @SerializedName("rssi") val rssi: Int,
    @SerializedName("safetyLock") val safetyLock: Boolean,
    @SerializedName("scheduleFinishTime") val scheduleFinishTime: Int,
    @SerializedName("schedulePauseRemainingTime") val schedulePauseRemainingTime: Int,
    @SerializedName("scheduleStartTime") val scheduleStartTime: Int,
    @SerializedName("stoveId") val stoveId: String,
    @SerializedName("stovePosition") val stovePosition: Int,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("updated") val updated: String,
    @SerializedName("userId") val userId: String
)

