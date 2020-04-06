package com.example.pankajoil.`interface`

import com.example.pankajoil.roomDatabase.OrderEntity

interface OnCartItemClickListner {
    fun onDelete(position:Int, size: Float, uniqueID:String)

    fun onUpdate(position: Int, order: OrderEntity, updatedQuantity:Int, updatedAmount:Int)
}