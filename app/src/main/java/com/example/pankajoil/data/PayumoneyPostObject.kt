package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class PayumoneyPostObject(
    @SerializedName("amount")
    var amount: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("fname")
    var fname: String,
    @SerializedName("key")
    var key: String,
    @SerializedName("pinfo")
    var pinfo: String,
    @SerializedName("salt")
    var salt: String,
    @SerializedName("txnid")
    var txnid: String,
    @SerializedName("udf5")
    var udf5: String
)