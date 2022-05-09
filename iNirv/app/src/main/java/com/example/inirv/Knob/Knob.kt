package com.example.inirv.Knob

class Knob(angle: Double, angles: IntArray, batteryLevel: Int, macID: String, id: String) {

    // MARK: Variables
    var mAngle: Double
    var mAngles: IntArray
    var mBatteryLevel: Int
    var mMacID: String
    var mID: String

    init {
        mAngle = angle
        mAngles = angles
        mBatteryLevel = batteryLevel
        mMacID = macID
        mID = id
    }
}