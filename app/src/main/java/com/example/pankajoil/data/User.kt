package com.example.pankajoil.data


import com.example.pankajoil.roomDatabase.OrderEntity
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class User(
    @SerializedName("_id")
    val id: String,
    val firstName: String,
    val lastName: String,
    var profileImage: String,
    val companyName: String,
    val mobileNumber: Long,
    val email: String,
    val password: String,
    val address: String,
    val gstin: String,
    var wishlistProducts: List<WishlistProducts>,
    var orders: List<Order>,
    var cartItems: ArrayList<OrderEntity>,
    @SerializedName("__v")
    val v: Int
)