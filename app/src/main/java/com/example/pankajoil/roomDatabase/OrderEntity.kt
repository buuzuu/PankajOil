package com.example.pankajoil.roomDatabase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_item")
data class OrderEntity(

    @NonNull@ColumnInfo(name = "uID") var uniqueID: String,
    @ColumnInfo(name = "name") var productName: String,
    @ColumnInfo(name = "volume") var size: Float,
    @ColumnInfo(name = "pieces") var quantity: Int,
    @ColumnInfo(name = "link") var url: String,
    @ColumnInfo(name = "price") var amount: Int,
    @ColumnInfo(name = "carton") var perCarton: Int


) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
