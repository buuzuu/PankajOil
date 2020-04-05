package com.example.pankajoil.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnGridItemClickListner
import com.example.pankajoil.`interface`.OnSliderItemClickListner
import com.example.pankajoil.data.Product
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderAdapter(ctx:Context, products: List<Product>?, var clickListner: OnSliderItemClickListner) : SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {
    var context:Context? = null
    var prodList:List<Product>? = null
    init {
        context = ctx
        prodList = products
    }


    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {

        val inflate = LayoutInflater.from(context).inflate(R.layout.image_slider_layout_item,null)
        val vh = SliderAdapterViewHolder(inflate)

        return vh

    }

    override fun getCount(): Int {
        return prodList!!.size
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
        viewHolder!!.initialize(prodList!![position],clickListner, position)

    }


    inner class SliderAdapterViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {

       var image:ImageView? = null
        init {
            image = itemView!!.findViewById(R.id.iv_auto_image_slider)
        }
        fun initialize(item:Product, action: OnSliderItemClickListner, index: Int){

            Picasso.get()
                .load(item.generalUrl).into(image)
            itemView.setOnClickListener {
                action.onSliderItemClick(item,getItemPosition(item))
            }
        }


    }
}