package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class TxnToken(
    @SerializedName("body")
    var body: Body,
    @SerializedName("head")
    var head: Head
)