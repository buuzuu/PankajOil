package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    val uniqueID: String,
    val productName: String,
    val variants: List<Variant>,
    val description: String,
    val generalUrl: String,
    @SerializedName("__v")
    val v: Int
)