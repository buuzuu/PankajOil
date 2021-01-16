package com.example.pankajoil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.`interface`.OnCartItemClickListner
import com.example.pankajoil.adapter.CartAdapter
import com.example.pankajoil.data.OrderItem
import com.example.pankajoil.data.UpdateCart
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.material.snackbar.Snackbar
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_cart.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Cart : AppCompatActivity(), OnCartItemClickListner {

    var item: OrderItem? = null
    private lateinit var toolbar: Toolbar
    val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
    lateinit var list: List<OrderEntity>
    private lateinit var dialogg: android.app.AlertDialog
    lateinit var cartAdapter: CartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.toolbar)
        Util.empty_Image = findViewById(R.id.empty_Image)
        Util.cart_Bottom = findViewById(R.id.bottom)
        setSupportActionBar(toolbar)
        dialogg = SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom)
            .setMessage("Please wait...").setCancelable(false).build()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        Util.cartItem = toolbar.findViewById(R.id.cartItem)
        cartItem.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        val layoutManager = LinearLayoutManager(this)
        //  layoutManager.stackFromEnd = true
        // layoutManager.reverseLayout = true
        orders_rv.layoutManager = layoutManager

        list = Util.user!!.cartItems
        if (list.isEmpty()) {
            setupViews(true, 0)
        } else {
            setupViews(false, list.size)
            loadCartData(list)
        }
        Util.cart_Bottom.setOnClickListener {

            if (TokenSharedPreference(this).isTokenPresent()) {
                startActivity(Intent(this, OrderSummary::class.java))
            } else {
                Snackbar.make(relativeLayout, "Please Sign In", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews(status: Boolean, size: Int) {
        if (status) {
            runOnUiThread {
                Util.empty_Image!!.visibility = View.VISIBLE
                Util.cart_Bottom!!.visibility = View.INVISIBLE
                Util.cartItem!!.text = "My Cart(${size})"

            }
        } else {
            runOnUiThread {
                Util.empty_Image!!.visibility = View.INVISIBLE
                Util.cart_Bottom!!.visibility = View.VISIBLE
                Util.cartItem!!.text = "My Cart(${size})"
            }
        }
    }

    private fun loadCartData(list: List<OrderEntity>) {

        cartAdapter = CartAdapter(list as ArrayList<OrderEntity>, this, this)
        orders_rv.adapter = cartAdapter
        cartAdapter.notifyDataSetChanged()

    }


    override fun onUpdate(position: Int, order: OrderEntity, updatedQuantity:Int,updatedAmount:Int) {
        var updatedOrder = order
        updatedOrder.quantity = updatedQuantity
        updatedOrder.amount = updatedAmount

        Util.startLoading(dialogg)
        val call = service.updateCartItem(
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey(),
            UpdateCart(updatedOrder.size,updatedOrder.uniqueID,updatedOrder.quantity,updatedOrder.amount)
        )
        call.enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Util.stopLoading(dialogg)
                Util.showToast(
                    this@Cart,
                    "Failed", 0
                )
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    cartAdapter.orderList[position] = updatedOrder
                    cartAdapter.notifyItemChanged(position,updatedOrder)
                    updateInLocalUsers(updatedOrder)
                }
                Util.stopLoading(dialogg)

            }
        })

    }

    fun updateInLocalUsers(orderEntity: OrderEntity){
        for ( (index, item) in Util.user!!.cartItems.withIndex()){
            if (item.size == orderEntity.size && item.uniqueID == orderEntity.uniqueID){
                Util.user!!.cartItems[index] = orderEntity
            }
        }
    }

    fun removeFromLocalUsers(size: Float, uniqueID: String) {
        for ( (index, item) in Util.user!!.cartItems.withIndex()){
            if (item.size == size && item.uniqueID == uniqueID){
                Util.user!!.cartItems.removeAt(index)
            }
        }
    }
    fun remove(orderEntity: OrderEntity, position: Int) {
        cartAdapter.orderList.remove(orderEntity)
        cartAdapter.notifyItemRemoved(position)
        Util.cartItem!!.text = "My Cart(${cartAdapter.orderList.size})"
        if (cartAdapter.orderList.isEmpty() || cartAdapter.orderList.size < 1) {
            Util.empty_Image!!.visibility = View.VISIBLE
            Util.cart_Bottom!!.visibility = View.INVISIBLE
        }
        Util.stopLoading(dialogg)
    }
    override fun onDelete(position: Int, size: Float, uniqueID: String) {

        Util.startLoading(dialogg)
        val call = service.deleteCartItem(
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey(),
            size,
            uniqueID
        )
        Log.d("TAG",TokenSharedPreference(this).getMobileNumber())
        Log.d("TAG",TokenSharedPreference(this).getAuthKey())
        Log.d("TAG", size.toString())
        Log.d("TAG",uniqueID)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Util.showToast(
                    this@Cart,
                    "Failed", 0
                )
                Util.stopLoading(dialogg)
            }
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                remove(cartAdapter.orderList[position],position)
                removeFromLocalUsers(size, uniqueID)
            }
        })
    }



}
