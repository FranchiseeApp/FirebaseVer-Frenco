package com.aryasurya.franchiso.data.pref

data class RegisterRequest(
    val userId: String,
    val email: String,
    val name: String,
    val username: String,
    val password: String,
    val role: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val name: String,
    val password: String,
    val role: String
)