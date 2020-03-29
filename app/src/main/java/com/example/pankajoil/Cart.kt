package com.example.pankajoil

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.adapter.CartAdapter
import com.example.pankajoil.data.OrderItem
import com.example.pankajoil.roomDatabase.OrderDAO
import com.example.pankajoil.roomDatabase.OrderDatabase
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.utils.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class Cart : AppCompatActivity() {

    var item: OrderItem? = null
    private lateinit var toolbar: Toolbar
    lateinit var database: OrderDatabase
    lateinit var dao: OrderDAO
    lateinit var list:List<OrderEntity>
    lateinit var cartAdapter: CartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        database = OrderDatabase.getInstance(this)
        dao = database.movieDao()
        toolbar = findViewById(R.id.toolbar)
        Util.empty_Image = findViewById(R.id.empty_Image)
        Util.cart_Bottom = findViewById(R.id.bottom)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        Util.cartItem = toolbar.findViewById(R.id.cartItem)
        cartItem.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        orders_rv.layoutManager = LinearLayoutManager(this)
        CoroutineScope(IO).launch {
            list = dao.getAllOrder()
            if (list.isEmpty()) {
                setupViews(true,0)
            } else {
                setupViews(false,list.size)
                loadCartData(list)
            }

        }
        Util.cart_Bottom.setOnClickListener {

            if (TokenSharedPreference(this).isTokenPresent()){
                startActivity(Intent(this,OrderSummary::class.java))

            }else{
                Snackbar.make(relativeLayout, "Please Sign In",Snackbar.LENGTH_SHORT).show()
            }



        }
   }

    override fun onPause() {
        super.onPause()
        CoroutineScope(IO).launch {
            list = dao.getAllOrder()
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

    private fun setupViews(status: Boolean, size:Int) {
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

        runOnUiThread {
            cartAdapter = CartAdapter(list as ArrayList<OrderEntity>,this)
            orders_rv.adapter = cartAdapter
            cartAdapter.notifyDataSetChanged()

        }
    }


}
