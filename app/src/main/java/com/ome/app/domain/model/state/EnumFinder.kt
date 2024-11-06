package com.ome.app.domain.model.state

import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.model.network.response.UserResponse

val Int?.stoveOrientation
    get() = StoveOrientation.entries.find { it.number == this }

val UserResponse?.stoveType
    get() = StoveType.entries.find { it.type == this?.stoveGasOrElectric && it.mounting == stoveKnobMounting }
val StoveRequest?.stoveType
    get() = StoveType.entries.find { it.type == this?.stoveGasOrElectric && it.mounting == stoveKnobMounting }

val String?.connectionState : ConnectionState
    get() = ConnectionState.entries.find { it.type == this } ?: ConnectionState.Offline

val Int?.rotation
    get() = when(this){
        1 -> Rotation.CLOCKWISE
        -1 -> Rotation.COUNTER_CLOCKWISE
        2 -> Rotation.DUAL
        else -> Rotation.CLOCKWISE
    }

val Int?.knobStatus
    get() = KnobStatus.entries.find { it.id == this } ?: KnobStatus.DoesNotExists

val String?.knobEntity : KnobEntity?
    get() = KnobEntity.entries.find { it.key == this }

val String?.mountingSurface: MountingSurface?
    get() = MountingSurface.entries.find { it.type == this }