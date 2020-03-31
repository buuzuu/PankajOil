package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Order(
    var items: List<Item>,
    var orderID: String,
    var orderDate: String,
    var totalAmount: Int,
    var companyName: String,
    var address: String,
    var paymentMode: String
)