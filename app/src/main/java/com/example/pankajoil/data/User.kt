package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

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
    val orders: List<Order>,
    @SerializedName("__v")
    val v: Int
)