package com.ome.app.domain.model.network.websocket


import com.google.gson.annotations.SerializedName

data class KnobPost(
    @SerializedName("angle")
    val angle: Double = 0.0,
    @SerializedName("battery")
    val battery: Double = 0.0,
    @SerializedName("batteryVolts")
    val batteryVolts: Double = 0.0,
    @SerializedName("calibrated")
    val calibrated: Boolean = false,
    @SerializedName("calibration")
    val calibration: Calibration = Calibration(),
    @SerializedName("connectStatus")
    val connectStatus: String = "",
    @SerializedName("firmwareVersion")
    val firmwareVersion: String = "",
    @SerializedName("gasOrElectric")
    val gasOrElectric: String = "",
    @SerializedName("ipAddress")
    val ipAddress: String = "",
    @SerializedName("lastScheduleCommand")
    val lastScheduleCommand: String = "",
    @SerializedName("macAddr")
    val macAddr: String = "",
    @SerializedName("mountingSurface")
    val mountingSurface: String = "",
    @SerializedName("rssi")
    val rssi: Double = 0.0,
    @SerializedName("scheduleFinishTime")
    val scheduleFinishTime: Double = 0.0,
    @SerializedName("schedulePauseRemainingTime")
    val schedulePauseRemainingTime: Double = 0.0,
    @SerializedName("scheduleStartTime")
    val scheduleStartTime: Double = 0.0,
    @SerializedName("stoveId")
    val stoveId: String = "",
    @SerializedName("stovePosition")
    val stovePosition: Double = 0.0,
    @SerializedName("temperature")
    val temperature: Double = 0.0,
    @SerializedName("updated")
    val updated: String = "",
    @SerializedName("userId")
    val userId: String = ""
){
    data class Calibration(
        @SerializedName("offAngle")
        val offAngle: Double = 0.0,
        @SerializedName("rotationDir")
        val rotationDir: Double = 0.0,
        @SerializedName("zones")
        val zones: List<Zone> = listOf()
    )

    data class Zone(
        @SerializedName("highAngle")
        val highAngle: Double = 0.0,
        @SerializedName("lowAngle")
        val lowAngle: Double = 0.0,
        @SerializedName("mediumAngle")
        val mediumAngle: Double = 0.0,
        @SerializedName("zoneName")
        val zoneName: String = "",
        @SerializedName("zoneNumber")
        val zoneNumber: Double = 0.0
    )
}