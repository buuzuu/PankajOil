package com.example.pankajoil.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.Cart
import com.example.pankajoil.R
import com.example.pankajoil.roomDatabase.OrderDAO
import com.example.pankajoil.roomDatabase.OrderDatabase
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.utils.Util
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.quantity_popup.*
import kotlinx.android.synthetic.main.quantity_popup.view.*
import kotlinx.android.synthetic.main.quantity_popup.view.apply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CartAdapter(val orderList: ArrayList<OrderEntity>, ctx: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    var context: Context
    var database: OrderDatabase = OrderDatabase.getInstance(ctx)
    var dao: OrderDAO

    init {
        dao = database.movieDao()
        context = ctx

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_layout, null)
        return CartViewHolder(view)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {


        holder.initialize(orderList[position])
        holder.remove.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    dao.deleteOrder(orderList[position])
                }catch (e:Exception){
                    Log.d("TAGG", e.message)
                }
            }
            notifyItemRemoved(position)
//            notifyDataSetChanged()
            orderList.remove(orderList[position])
            try {
                Util.cartItem!!.text = "My Cart(${orderList.size})"
                if (orderList.isEmpty() || orderList.size <1){
                    Util.empty_Image!!.visibility = View.VISIBLE
                    Util.cart_Bottom!!.visibility = View.INVISIBLE

                }

            }catch (e:Exception){
                Util.cartItem!!.text = "My Cart"
            }


}
        holder.quantity.setOnClickListener {
            populateView(position)
        }

    }
    fun populateView(position: Int){
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.quantity_popup, null)
        val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.apply.setOnClickListener {
            if (mAlertDialog.value.text.isNullOrEmpty()){
                mAlertDialog.dismiss()
            }else{
                var updateOrder = orderList[position]
                updateOrder.quantity = mDialogView.value.text.toString().toInt()
                updateOrder.amount = updateOrder.quantity * getBasePrice(updateOrder.uniqueID, updateOrder.size)
                CoroutineScope(Dispatchers.IO).launch {
                    dao.updateOrder(updateOrder)
                }
                notifyItemChanged(position,updateOrder)
//                notifyDataSetChanged()
                mAlertDialog.dismiss()
            }

        }
    }
    fun getBasePrice(uniqueID:String, volume:Float):Int{

        for( product in Util.products!!){
            if (product.uniqueID == uniqueID){
                for (variant in product.variants){
                    if (variant.size == volume){
                        return variant.price
                    }
                }
            }
        }
        return 0

    }


    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val quantity: ConstraintLayout = itemView.findViewById(R.id.quantity)
        val name: TextView = itemView.findViewById(R.id.name)
        private val volume: TextView = itemView.findViewById(R.id.volume)
        val price: TextView = itemView.findViewById(R.id.price)
        val pieces: TextView = itemView.findViewById(R.id.pieces)
        val remove: TextView = itemView.findViewById(R.id.remove)
        val image: ImageView = itemView.findViewById(R.id.image)

        fun initialize(order: OrderEntity) {
            name.text = order.productName
            volume.text = "${order.size.toString()} liters"
            Util.qty!!.text = order.quantity.toString()
            pieces.text = "${order.perCarton.toString()} pieces per carton"
            Picasso.get().load(order.url).into(image)
            price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(order.amount)}"


        }

        init {
            Util.qty = itemView.findViewById(R.id.qty)
            itemView.setOnClickListener(this)


        }
        override fun onClick(v: View?) {


        }



    }
}