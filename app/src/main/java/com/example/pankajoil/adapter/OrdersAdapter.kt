package com.example.pankajoil.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.data.Item
import com.example.pankajoil.data.Order
import com.example.pankajoil.utils.Util
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class OrdersAdapter(var list:List<Order>): RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    var prodPrice: Int? = 0
    var igstPrice: Double? =null
    var cstPrice: Double? = null
    var finalPrice: Double? = null
    var subAdapter: SubOrderAdapter?=null
    var pool:RecyclerView.RecycledViewPool?=null
    init {
        pool = RecyclerView.RecycledViewPool()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_layout, null)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        for (item in list[position].items){
            Log.e("OR",item.amount.toString())
            prodPrice = item.amount + prodPrice!!
            igstPrice = (0.09 * prodPrice!!)
            cstPrice = (0.09 * prodPrice!!)

        }
        finalPrice = (prodPrice!! + igstPrice!! + cstPrice!!)
        holder.orderID.text = list[position].orderID
        holder.orderDate.text = list[position].orderDate
        holder.userName.text =  Util.user!!.firstName+" "+Util.user!!.lastName
        holder.shopName.text = Util.user!!.companyName
        holder.address.text = Util.user!!.address
        holder.mobileNumber.text = Util.user!!.mobileNumber.toString()
        holder.productPrice.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(prodPrice)}"
        holder.igst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(igstPrice!!)}"
        holder.cst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(cstPrice!!)}"
        holder.payableAmount.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(finalPrice!!.roundToInt())}"

        subAdapter = SubOrderAdapter(list[position].items)
        holder.subRecyclerView.adapter = subAdapter
        holder.subRecyclerView.setRecycledViewPool(pool)
        prodPrice = 0
        igstPrice = 0.0
        cstPrice = 0.0
        finalPrice = 0.0
    }

    inner class OrderViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var subRecyclerView:RecyclerView = itemView.findViewById(R.id.subOrderRv)
        var orderID:TextView = itemView.findViewById(R.id.orderID)
        var orderDate:TextView = itemView.findViewById(R.id.orderDate)
        var userName:TextView = itemView.findViewById(R.id.userName)
        var shopName:TextView = itemView.findViewById(R.id.shopName)
        var address:TextView = itemView.findViewById(R.id.address)
        var mobileNumber:TextView = itemView.findViewById(R.id.mobileNumber)
        var productPrice:TextView = itemView.findViewById(R.id.productPrice)
        var igst:TextView = itemView.findViewById(R.id.igst)
        var cst:TextView = itemView.findViewById(R.id.cst)
        var payableAmount:TextView = itemView.findViewById(R.id.payableAmount)
        init {
            subRecyclerView.setHasFixedSize(true)
            subRecyclerView.isNestedScrollingEnabled = false
            subRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            subRecyclerView.itemAnimator =DefaultItemAnimator()
        }



    }

}