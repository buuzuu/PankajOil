package com.example.pankajoil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.data.Item
import java.text.NumberFormat
import java.util.*

class SubOrderAdapter(var list: List<Item>):RecyclerView.Adapter<SubOrderAdapter.SubViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_order_layout, null)
        return SubViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
        holder.productName.text = list[position].productName +"  "+list[position].size.toString()+" ℓ"
        holder.productAmount.text =  "₹ ${NumberFormat.getNumberInstance(Locale.US).format(list[position].amount)}"
        holder.productQuantity.text = list[position].quantity.toString() +" pcs"
    }

    class SubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        var productAmount: TextView = itemView.findViewById(R.id.productAmount)


    }
}