package com.ome.app.domain.model.state

enum class StoveType(val type: String, val mounting: String){
    GAS_TOP("gas", "horizontal"),
    ELECTRIC_TOP("electric", "horizontal"),
    GAS_RANGE("gas", "vertical"),
    ELECTRIC_RANGE("electric", "vertical")
}


