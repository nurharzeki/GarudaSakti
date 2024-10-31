package com.example.garudasakti.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    val message: String?,
    val created_account: CreatedAccount?,

    @SerializedName("token")
    val token: String? = null,
    val errors: Map<String, List<String>>?
)

data class CreatedAccount(
    val id: Int,
    val username: String,
    val name: String,
    val email: String,
    val saldo: Int,
    val poin: Int,
    val created_at: String,
    val updated_at: String
)
