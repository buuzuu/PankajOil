package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("_id")
    val id: String,
    val productName: String,
    val quantity: Int,
    val url: String,
    val amount: Int
)