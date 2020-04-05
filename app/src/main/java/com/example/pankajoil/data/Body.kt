package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("authenticated")
    var authenticated: Boolean,
    @SerializedName("isPromoCodeValid")
    var isPromoCodeValid: Boolean,
    @SerializedName("resultInfo")
    var resultInfo: ResultInfo,
    @SerializedName("txnToken")
    var txnToken: String
)