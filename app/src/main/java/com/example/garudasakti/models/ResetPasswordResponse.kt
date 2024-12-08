package com.example.garudasakti.models

data class ResetPasswordResponse(
    val message: String,
    val token: String,
    val customer: CustomerUser
)
