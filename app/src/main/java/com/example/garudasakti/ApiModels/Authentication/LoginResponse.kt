package com.example.garudasakti.ApiModels.Authentication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("user")
    val user: User? = null,

    @SerializedName("token")
    val token: String? = null
)

data class User(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("saldo")
    val saldo: Int? = null,

    @SerializedName("poin")
    val poin: Int? = null
)