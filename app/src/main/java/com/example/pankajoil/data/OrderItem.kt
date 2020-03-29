package com.example.pankajoil.data

import java.io.Serializable

data class OrderItem(
    var uniqueID: String,
    var productName: String,
    var size: Float,
    var quantity: Int,
    var url: String,
    var amount: Int
):Serializable