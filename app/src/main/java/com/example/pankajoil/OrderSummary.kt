package com.example.pankajoil

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.adapter.SummaryCartAdapter
import com.example.pankajoil.data.Item
import com.example.pankajoil.data.Order
import com.example.pankajoil.roomDatabase.OrderDAO
import com.example.pankajoil.roomDatabase.OrderDatabase
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class OrderSummary : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    lateinit var stepView: StepView
    lateinit var database: OrderDatabase
    lateinit var list: List<OrderEntity>
    lateinit var dao: OrderDAO
    var prodPrice: Int? = 0
    var igstPrice: Double? = null
    var cstPrice: Double? = null
    var finalPrice: Float = 0.0f
    lateinit var price: TextView
    lateinit var igst: TextView
    lateinit var cst: TextView
    lateinit var final: TextView
    var i = 0
    lateinit var service:APIServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        toolbar = findViewById(R.id.toolbar)
        database = OrderDatabase.getInstance(this)
        dao = database.movieDao()
        price = findViewById(R.id.productPrice)
        igst = findViewById(R.id.igst)
        cst = findViewById(R.id.cst)
        final = findViewById(R.id.payableAmount)
        setSupportActionBar(toolbar)

        service= Util.generalRetrofit.create(APIServices::class.java)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        stepView = findViewById(R.id.step_view)
        stepView.go(1, true)
        setupUserDetails()
        orderSummaryRV.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            list = dao.getAllOrder()
            Log.d("TAGG", list.size.toString())
            computeProductPrice(list)
            orderSummaryRV.adapter = SummaryCartAdapter(list)

        }

        placeOrder.setOnClickListener {
            if (i == 0) {
                Toast.makeText(this, "Please select payment medium", Toast.LENGTH_SHORT).show()
            } else if (i == 1) {
                createOrder()
                Toast.makeText(this, "Pay now", Toast.LENGTH_SHORT).show()
            } else if (i == 2) {
                createOrder()
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

    private fun setupUserDetails() {
        if (Util.user != null) {
            gstin.text = Util.user!!.gstin
            userName.text = Util.user!!.firstName + " " + Util.user!!.lastName
            shopName.text = Util.user!!.companyName
            address.text = Util.user!!.address
            mobileNumber.text = Util.user!!.mobileNumber.toString()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.now ->
                    if (checked) {
                        i = 1
                    }
                R.id.later ->
                    if (checked) {
                        i = 2
                    }
            }
        }
    }
    private fun computeProductPrice(list: List<OrderEntity>) {

        for (order in list) {
            prodPrice = prodPrice?.plus(order.amount)
        }

        if (prodPrice != null) {
            igstPrice = (0.09 * prodPrice!!)
            cstPrice = (0.09 * prodPrice!!)
            finalPrice = (prodPrice!! + igstPrice!! + cstPrice!!).toFloat()
        }
//        "₹ ${NumberFormat.getNumberInstance(Locale.US).format(order.amount)}"
        price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(prodPrice)}"
        igst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(igstPrice)}"
        cst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(cstPrice)}"
        final.text =
            "₹ ${NumberFormat.getNumberInstance(Locale.US).format(finalPrice.roundToInt())}"


    }


    fun createOrder() {

        val order = Order(generateOrderItem(),"AS34DF56TR",
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString(),
            finalPrice.roundToInt(),
            Util.user!!.companyName,Util.user!!.address, getPaymentStatus())
        val call = service.addOrder(order,TokenSharedPreference(this).getMobileNumber(),TokenSharedPreference(this).getAuthKey() )
        call.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@OrderSummary, "Order Posted", Toast.LENGTH_SHORT).show()
              //  var tempList = ArrayList<Order>()
                var tempList = Util.user!!.orders as ArrayList<Order>
                tempList.add(order)
                Util.user!!.orders = tempList
            }

        })


    }
    fun generateOrderItem():List<Item>{
        var itemList:ArrayList<Item> = ArrayList<Item>()
        for ( i in list){
            itemList.add(Item(i.productName,i.size,i.quantity,i.url,i.amount))
        }
        return itemList
    }

    fun getPaymentStatus():String{
        if(i == 1){
            return "online"
        }else{
            return "cash"
        }
    }
}