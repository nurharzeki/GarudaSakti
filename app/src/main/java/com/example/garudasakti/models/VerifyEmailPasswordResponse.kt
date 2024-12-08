package com.example.garudasakti.models

data class VerifyEmailPasswordResponse(
    val message: String,
    val customer: CustomerUser
)

data class CustomerUser(
    val name: String,
    val username: String,
    val email: String,
)