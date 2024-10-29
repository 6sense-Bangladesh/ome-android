package com.ome.app.domain.model.network.response

data class KnobOwnershipResponse(
    val status: Int?,
    val statusName: String?
)

enum class KnobStatus(val id: Int){
    InUsedByAnotherUser(0),
    NotInUse(1),
    InUseByYou(2),
    DoesNotExists(3)
}

val Int?.knobStatus
    get() = KnobStatus.entries.find { it.id == this } ?: KnobStatus.DoesNotExists
