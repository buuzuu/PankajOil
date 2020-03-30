package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Item(

    val productName: String,
    val size:Float,
    val quantity: Int,
    val url: String,
    val amount: Int
)