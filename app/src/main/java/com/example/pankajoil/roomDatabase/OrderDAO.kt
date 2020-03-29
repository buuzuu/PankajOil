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

    @Delete
    suspend fun deleteOrder(item: OrderEntity)



}