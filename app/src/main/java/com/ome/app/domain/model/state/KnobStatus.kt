package com.ome.app.domain.model.state

enum class KnobStatus(val id: Int){
    InUsedByAnotherUser(0),
    NotInUse(1),
    InUseByYou(2),
    DoesNotExists(3)
}