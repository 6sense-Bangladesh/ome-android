package com.example.inirv.Knob

import java.util.*

data class KnobZone(
    val zoneNumber: Int,
    val zoneName: String,
    val rotationDir: Int,
    val lowAngle: Int,
    val medAngle: Int,
    val highAngle: Int
)

class Knob(
    macID: String,
    safetyLockEnabled: Boolean,
    stoveID: String,
    userID: String,
    firmwareVersion: String,
    stovePosition: Int,
    ipAddress: String,
    currLevel: Int = 0,
    angles: Array<Int> = arrayOf(0,90,180,270),
    lastScheduledCommand: String = "",
    timerValue: Int = 0,
    timerDate: Date = Date(),
    schedulePauseRemainingTime: Int = -1,
    timerPaused: Boolean = false,
    connection: String = "",
    calibrated: Boolean = false,
    batteryLevel: Int = 0
) {

    // MARK: Variables
    var mAngles: Array<Int>
    var mMacID: String
    var mIsOff: Boolean = false
    var mSafetyLockEnabled: Boolean
    var mStoveID: String
    var mUserID: String
    var mFirmwareVersion: String
    var mStovePosition: Int
    var mCurrLevel: Int
    var mZones: List<KnobZone> = listOf()
    var mOffAngle: Int = -1
    var mTimerPaused: Boolean
    var mCalibrated: Boolean
    var mSchedulePauseRemainingTime: Int
    var mConnection: String
    var mBatteryLevel: Int

    init {
        mAngles = angles
        mMacID = macID
        mSafetyLockEnabled = safetyLockEnabled
        mStoveID = stoveID
        mUserID = userID
        mFirmwareVersion = firmwareVersion
        mStovePosition = stovePosition
        mCurrLevel = currLevel
        mTimerPaused = timerPaused
        mCalibrated = calibrated
        mSchedulePauseRemainingTime = schedulePauseRemainingTime
        mConnection = connection
        mBatteryLevel = batteryLevel
    }
}