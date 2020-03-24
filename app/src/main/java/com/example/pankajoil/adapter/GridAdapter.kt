package com.example.pankajoil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnGridItemClickListner
import com.example.pankajoil.data.Product
import com.squareup.picasso.Picasso

class GridAdapter(ctx: Context, products: List<Product>?, var clickListner: OnGridItemClickListner) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    var context:Context? = null
    var prodList:List<Product>? = null
    init {
        context = ctx
        prodList = products
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout,null)
        return GridViewHolder(view)
    }

    override fun getItemCount(): Int {
        return prodList!!.size
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.initialize(prodList!![position], clickListner)
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = null
        var imageText: TextView? = null
        init {
            image = itemView.findViewById(R.id.item_image)
            imageText = itemView.findViewById(R.id.item_text)

        }
        fun initialize(item:Product, action: OnGridItemClickListner){
            imageText!!.text = item.productName
            Picasso.get().load(item.generalUrl).into(image)
            itemView.setOnClickListener {
                action.onGridItemClick(item,adapterPosition)
            }
        }

    }

}
