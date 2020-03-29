package com.example.pankajoil

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.adapter.SummaryCartAdapter
import com.example.pankajoil.roomDatabase.OrderDAO
import com.example.pankajoil.roomDatabase.OrderDatabase
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.utils.Util
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class OrderSummary : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    lateinit var stepView: StepView
    lateinit var database: OrderDatabase
    lateinit var list:List<OrderEntity>
    lateinit var dao: OrderDAO
    var prodPrice:Int?= 0
    var igstPrice:Double?= null
    var cstPrice:Double?= null
    var finalPrice:Float =0.0f
    lateinit var price:TextView
    lateinit var igst:TextView
    lateinit var cst:TextView
    lateinit var final:TextView

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

    private fun setupUserDetails(){
        if (Util.user !=null){
            gstin.text = Util.user!!.gstin
            userName.text = Util.user!!.firstName +" " +Util.user!!.lastName
            shopName.text = Util.user!!.companyName
            address.text = Util.user!!.address
            mobileNumber.text = Util.user!!.mobileNumber.toString()
        }
    }
    private fun  computeProductPrice(list:List<OrderEntity>) {

        for ( order in list){
            prodPrice = prodPrice?.plus(order.amount)
        }

        if (prodPrice !=null){
            igstPrice = (0.09 * prodPrice!!)
            cstPrice = (0.09 * prodPrice!!)
            finalPrice = (prodPrice!! + igstPrice!! + cstPrice!!).toFloat()
        }
//        "₹ ${NumberFormat.getNumberInstance(Locale.US).format(order.amount)}"
        price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(prodPrice)}"
        igst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(igstPrice)}"
        cst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(cstPrice)}"
        final.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(finalPrice)}"





    }
}