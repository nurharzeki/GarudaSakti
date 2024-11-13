package com.example.garudasakti.models

data class PemesananLangsungRequest(
    val nama_tim: String,
    val lapangan_id: Int,
    val tanggal: String,
    val jam: List<String>
)
