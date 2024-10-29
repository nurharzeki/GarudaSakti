package com.example.garudasakti.models

import com.google.gson.annotations.SerializedName

data class PesananSaya(
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("lapangan_name") val lapangan: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("jam") val jam: String
)