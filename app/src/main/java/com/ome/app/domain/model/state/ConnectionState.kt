package com.ome.app.domain.model.state

enum class ConnectionState(val type: String){
    Online("online"),
    Offline("offline"),
    Charging("charging")
}

