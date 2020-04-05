package com.example.pankajoil.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [OrderEntity::class], version = 1)
abstract class OrderDatabase: RoomDatabase() {
    abstract fun movieDao(): OrderDAO

    companion object {
        @Volatile
        private var INSTANCE: OrderDatabase? = null

        fun getInstance(context: Context): OrderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(OrderDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderDatabase::class.java,
                    "order_database"
                )
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}