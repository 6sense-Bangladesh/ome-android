package com.ome.app.domain.model.state

enum class KnobEntity(val key: String) {
    ANGLE("angle"),
    MOUNTING_SURFACE("mountingSurface"),
    BATTERY("battery"),
    TEMPERATURE("temperature"),
    RSSI("rssi"),
    KNOB_REPORTED_SCHEDULE_STOP("knobReportedScheduleStop"),
    CONNECT_STATUS("connectStatus"),
    CONNECT_IP_ADD("connectIpAddr"),
    FIRMWARE_VERSION("firmwareVersion"),

    KNOB_POST("knobPost"),
    KNOB_PATCH("knobPatch"),
    KNOB_SET_CALIBRATION("knobSetCalibration"),
    KNOB_DELETE("knobDelete"),

    USER_POST("userPost"),
    USER_PATCH("userPatch"),
    USER_DELETE("userDelete")
}