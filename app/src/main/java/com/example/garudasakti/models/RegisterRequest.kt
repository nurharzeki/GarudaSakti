package com.example.garudasakti.models

data class RegisterRequest(
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
)
