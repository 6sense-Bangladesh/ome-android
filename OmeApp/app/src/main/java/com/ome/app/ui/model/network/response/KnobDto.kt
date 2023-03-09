package com.ome.app.model.network.response

import com.ome.app.ui.model.network.response.CalibrationDto

data class KnobDto(
    val angle: Int,
    val battery: Int,
    val batteryVolts: Double,
    val calibrated: Boolean,
    val calibration: CalibrationDto,
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
)
