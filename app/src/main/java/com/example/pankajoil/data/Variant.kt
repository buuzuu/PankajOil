package com.example.pankajoil.data


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Variant(
    @SerializedName("_id")
    val id: String,
    val size: Float,
    val price: Int,
    val perCarton: Int,
    val url: String
):Serializable