package com.ome.app.data.remote.websocket

import com.ome.app.model.network.websocket.KnobMessageEvent
import com.tinder.scarlet.ws.Receive
import kotlinx.coroutines.flow.Flow

interface KnobWebSocketService {

    @Receive
    fun knobMessageEvent(): Flow<KnobMessageEvent>

//    @Receive
//    fun knobAngle(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobMountingSurface(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobBattery(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobTemperature(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobRssi(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobConnectIpAddress(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knobReportedScheduleStop(): Flow<KnobConnectionStatus>
//
//    @Receive
//    fun knoFirmwareVersion(): Flow<KnobConnectionStatus>
}
