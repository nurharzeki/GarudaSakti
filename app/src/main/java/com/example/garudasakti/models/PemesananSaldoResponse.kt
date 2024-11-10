package com.example.garudasakti.models

import com.google.gson.annotations.SerializedName

data class PemesananSaldoResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PemesananData
)

data class PemesananData(
    @SerializedName("id") val id: Int,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("lapangan_id") val lapanganId: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam") val jam: String,
    @SerializedName("nama_tim") val namaTim: String,
    @SerializedName("saldo_terakhir") val saldoTerakhir: Int,
    @SerializedName("poin_terakhir") val poinTerakhir: Int
)