package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName

data class ResultInfo(
    @SerializedName("resultCode")
    var resultCode: String,
    @SerializedName("resultMsg")
    var resultMsg: String,
    @SerializedName("resultStatus")
    var resultStatus: String
)