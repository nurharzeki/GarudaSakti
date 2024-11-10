package com.example.garudasakti.models

import com.google.gson.annotations.SerializedName

data class PemesananPoinResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PemesananPoinData
)

data class PemesananPoinData(
    @SerializedName("id") val id: Int,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("lapangan_id") val lapanganId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam") val jam: String,
    @SerializedName("nama_tim") val namaTim: String,
    @SerializedName("poin_terakhir") val poinTerakhir: Int
)