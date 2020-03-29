package com.example.pankajoil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.roomDatabase.OrderEntity
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class SummaryCartAdapter(val orderList:List<OrderEntity>) : RecyclerView.Adapter<SummaryCartAdapter.SummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.summary_cart_layout, null)
        return SummaryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
   }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.initialize(orderList[position])
    }

    inner class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val volume: TextView = itemView.findViewById(R.id.volume)
        val pieces: TextView = itemView.findViewById(R.id.pieces)
        val price: TextView = itemView.findViewById(R.id.price)
        val qty: TextView = itemView.findViewById(R.id.qty)
        val image: ImageView = itemView.findViewById(R.id.image)

        fun initialize(order: OrderEntity) {
            name.text = order.productName
            volume.text = "${order.size.toString()} liters"
            qty.text = order.quantity.toString()
            pieces.text = "${order.perCarton.toString()} pieces per carton"
            Picasso.get().load(order.url).into(image)
            price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(order.amount)}"


        }

    }
}