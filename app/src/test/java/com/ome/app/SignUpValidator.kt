package com.ome.app

import com.ome.app.utils.isValidPassword
import com.ome.app.utils.isValidPhoneUS

data class MockUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
)

object SignUpValidator {
    val userDB = listOf(
        MockUser(
            firstName = "John",
            lastName = "Doe",
            email = "john.mclean@examplepetstore.com",
            phoneNumber = "15552345678",
            password = "Password1234"
        ),
        MockUser(
            firstName = "Jane",
            lastName = "Doe",
            email = "james.c.mcreynolds@example-pet-store.com",
            phoneNumber = "15552345688",
            password = "Password12342"
        )
    )

    fun createUser(
        userName: String,
        email: String,
        phoneNumber: String = "",
        password: String,
        confirmPassword: String,
    ): Boolean {
        if (userName.isBlank()) {
            return false
        }
        if (userDB.any { it.email == email } && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$".toRegex())) {
            return false
        }
        if (!phoneNumber.isValidPhoneUS()){
            return false
        }
        if (!password.isValidPassword()){
            return false
        }
        if (password != confirmPassword){
            return false
        }
        return true
    }
}