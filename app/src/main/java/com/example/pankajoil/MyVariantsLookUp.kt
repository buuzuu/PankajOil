package com.example.pankajoil

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.adapter.VariantAdapter

class MyVariantsLookUp(val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view !=null){

//            val holder = recyclerView.getChildViewHolder(view)
//            if (holder is VariantAdapter.VariantViewHolder){
//                return object :ItemDetails<Long>(){
//                    override fun getSelectionKey(): Long? {
//                       return holder.itemId
//                    }
//                    override fun getPosition(): Int {
//                         return holder.adapterPosition
//                    }
//
//                }
//            }
            return (recyclerView.getChildViewHolder(view) as VariantAdapter.VariantViewHolder)
                .getItemDetails()



        }
        return null
    }


}