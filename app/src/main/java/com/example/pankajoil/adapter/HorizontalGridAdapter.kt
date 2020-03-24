package com.example.pankajoil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnHorizontalGridItemClickListner
import com.example.pankajoil.data.Product
import com.squareup.picasso.Picasso

class HorizontalGridAdapter(ctx:Context, product: List<Product>?, var clickListner: OnHorizontalGridItemClickListner) :
    RecyclerView.Adapter<HorizontalGridAdapter.HorizontalGridViewHolder>() {
    var context:Context? = null
    private var prodList:List<Product>? = null
    init {
        context = ctx
        prodList = product
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalGridViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.horizontal_grid_item_layout,null)
        return HorizontalGridViewHolder(view)
    }

    override fun getItemCount(): Int {
        return prodList!!.size
    }

    override fun onBindViewHolder(holder: HorizontalGridViewHolder, position: Int) {
        holder.initialize(prodList!![position], clickListner)
    }

    class HorizontalGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = null
        var productName: TextView? = null
        var productPrice: TextView? = null
        init {
            image = itemView.findViewById(R.id.productImage)
            productName = itemView.findViewById(R.id.name)
            productPrice = itemView.findViewById(R.id.price)
        }

        fun initialize(item:Product, action: OnHorizontalGridItemClickListner){

            productName!!.text = item.productName
            productPrice!!.text = "₹ ${item.variants[0].price.toString()}"
            Picasso.get()
                .load(item.generalUrl).into(image)

            itemView.setOnClickListener {
                action.onHorizontalGridItemClick(item,adapterPosition)
            }
        }

    }
}