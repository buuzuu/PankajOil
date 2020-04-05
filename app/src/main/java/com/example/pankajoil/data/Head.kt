package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class Head(
    @SerializedName("responseTimestamp")
    var responseTimestamp: String,
    @SerializedName("signature")
    var signature: String,
    @SerializedName("version")
    var version: String
)