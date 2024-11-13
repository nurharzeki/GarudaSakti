package com.example.garudasakti.models

data class PemesananLangsungResponse(
    val snap_token: String,
    val transaction_data: TransactionData
)

data class TransactionData(
    val transaction_details: TransactionDetails,
    val customer_details: CustomerDetails,
    val item_details: List<ItemDetails>
)

data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int
)

data class CustomerDetails(
    val first_name: String,
    val email: String
)

data class ItemDetails(
    val id: Int,
    val price: Int,
    val quantity: Int,
    val name: String
)

