package com.ome.app

object SignInValidator {
    data class MockUser(val userEmail: String, val password: String)


    private val mockUserDB = listOf(
        MockUser("jmaessmith@gmail.com", "123456"),
        MockUser("clerknight@gmail.com", "123456"),
        MockUser("theodore.roosevelt@altostrat.com", "123")
    )

    fun validateSignIn(userEmail: String, password: String): Boolean {
        if (userEmail.isBlank() || password.isBlank()) return false
        if (!userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$".toRegex())) return false
        mockUserDB.forEach {
            if (it.userEmail == userEmail && it.password == password) return true
        }
        return false
    }
}