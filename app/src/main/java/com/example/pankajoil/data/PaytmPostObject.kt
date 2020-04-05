package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class PaytmPostObject(
    @SerializedName("callbackUrl")
    var callbackUrl: String,
    @SerializedName("checksum")
    var checksum: String,
    @SerializedName("currency")
    var currency: String,
    @SerializedName("custId")
    var custId: String,
    @SerializedName("mid")
    var mid: String,
    @SerializedName("orderId")
    var orderId: String,
    @SerializedName("requestType")
    var requestType: String,
    @SerializedName("value")
    var value: String,
    @SerializedName("websiteName")
    var websiteName: String
)