package com.example.appquanly

object AuthManager {
    private val userDatabase = mutableMapOf<String, String>() // email -> password

    fun register(email: String, password: String): Boolean {
        if (userDatabase.containsKey(email)) return false
        userDatabase[email] = password
        return true
    }

    fun login(email: String, password: String): Boolean {
        return userDatabase[email] == password
    }
}
