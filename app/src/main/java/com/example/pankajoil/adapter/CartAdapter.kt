package com.example.pankajoil.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnCartItemClickListner
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.utils.Util
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.quantity_popup.*
import kotlinx.android.synthetic.main.quantity_popup.view.*
import java.text.NumberFormat
import java.util.*

class CartAdapter(val orderList: ArrayList<OrderEntity>, ctx: Context, var listner: OnCartItemClickListner) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    var context: Context = ctx


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_layout, null)
        return CartViewHolder(view)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.initialize(orderList[position], listner)


    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val quantity: ConstraintLayout = itemView.findViewById(R.id.quantity)
        val name: TextView = itemView.findViewById(R.id.name)
        private val volume: TextView = itemView.findViewById(R.id.volume)
        private val qty: TextView = itemView.findViewById(R.id.qty)
        val price: TextView = itemView.findViewById(R.id.price)
        val pieces: TextView = itemView.findViewById(R.id.pieces)
        val remove: TextView = itemView.findViewById(R.id.remove)
        val image: ImageView = itemView.findViewById(R.id.image)

        fun initialize(
            order: OrderEntity,
            listner: OnCartItemClickListner
        ) {
            name.text = order.productName
            volume.text = "${order.size.toString()} liters"
            qty!!.text = order.quantity.toString()
            pieces.text = "${order.perCarton.toString()} pieces per carton"
            Picasso.get().load(order.url).into(image)
            price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(order.amount)}"
            remove.setOnClickListener {
                val mBuilder = AlertDialog.Builder(context)
                mBuilder.setMessage("Do you want to delete ?")
                mBuilder.setCancelable(false)
                mBuilder.setPositiveButton("Yes"
                ) { dialog, which -> listner.onDelete(adapterPosition, order.size,order.uniqueID) }
                mBuilder.setNegativeButton("No"
                ) { dialog, which -> dialog.dismiss() }

                val mAlertDialog = mBuilder.show()

            }
            quantity.setOnClickListener {
                populateView(order)
            }
        }

        init {
            itemView.setOnClickListener(this)

        }
        override fun onClick(v: View?) {

        }
        fun populateView(order: OrderEntity) {
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

                    listner.onUpdate(adapterPosition,order
                        ,mDialogView.value.text.toString().toInt()
                        ,mDialogView.value.text.toString().toInt() * getBasePrice(order.uniqueID, order.size) )

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








    }
}