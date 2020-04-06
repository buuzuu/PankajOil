package com.example.pankajoil.roomDatabase



data class OrderEntity(

    var uniqueID: String,
    var productName: String,
     var size: Float,
    var quantity: Int,
    var url: String,
    var amount: Int,
    var perCarton: Int

)