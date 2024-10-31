package com.example.garudasakti.models

data class UpdateProfilResponse(
    val message: String,
    val data: ProfilData?,
    val errors: Map<String, List<String>>?
)

data class ProfilData(
    val name: String,
    val username: String,
    val email: String
)