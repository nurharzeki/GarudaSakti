package com.example.garudasakti.models

data class MemberResponse(
    val membership_status : Int,
    val message : String,
    val data : Customer?
)

data class Customer(
    val customer_name: String,
    val saldo: Int,
    val poin: Int
)
