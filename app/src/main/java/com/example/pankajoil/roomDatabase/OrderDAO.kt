package com.example.pankajoil.roomDatabase

import androidx.room.*

@Dao
interface OrderDAO {



    @Query("SELECT * FROM order_item")
    fun getAllOrder(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrder(orderItem: OrderEntity)

    @Update
    suspend fun updateOrder(item: OrderEntity)

    @Query("DELETE FROM order_item WHERE uID = :item")
    suspend fun deleteOrder(item: String)



}