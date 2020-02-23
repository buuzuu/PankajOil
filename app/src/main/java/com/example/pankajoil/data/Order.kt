package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Order(
    val items: List<Item>,
    @SerializedName("_id")
    val id: String,
    val orderID: String,
    val totalAmount: Int,
    val companyName: String,
    val address: String,
    val paymentMode: String
)