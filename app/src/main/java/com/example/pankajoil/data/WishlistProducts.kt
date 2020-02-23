package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class WishlistProducts(
    @SerializedName("_id")
    val id: String,
    val uniqueID: String,
    val productName: String,
    val generalUrl: String
)