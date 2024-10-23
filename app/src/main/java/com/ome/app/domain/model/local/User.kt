package com.ome.app.domain.model.local

data class User(
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = "",
    var accessToken: String = "",
    var email: String = ""
)
