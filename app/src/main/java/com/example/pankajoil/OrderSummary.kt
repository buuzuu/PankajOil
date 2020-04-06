package com.example.pankajoil

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidbyme.dialoglib.DroidDialog
import com.example.pankajoil.adapter.SummaryCartAdapter
import com.example.pankajoil.data.Item
import com.example.pankajoil.data.Order
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.shuhart.stepview.StepView
import dmax.dialog.SpotsDialog
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
    lateinit var list: List<OrderEntity>

    var prodPrice: Int? = 0
    var igstPrice: Double? = null
    var cstPrice: Double? = null
    var finalPrice: Float = 0.0f
    var orderID: String = ""
    lateinit var price: TextView
    lateinit var igst: TextView
    lateinit var cst: TextView
    lateinit var final: TextView
    var i = 0
    var UPI_PAYMENT = 0
    val GOOGLE_PAY_REQUEST_CODE = 123

    private var requestCODE = 24
    lateinit var service: APIServices
    private lateinit var dialog: android.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        toolbar = findViewById(R.id.toolbar)

        orderID = generateOrderID()
        price = findViewById(R.id.productPrice)
        igst = findViewById(R.id.igst)
        cst = findViewById(R.id.cst)
        final = findViewById(R.id.payableAmount)
        setSupportActionBar(toolbar)
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom)
            .setMessage("Wait...").setCancelable(false).build()
        service = Util.generalRetrofit.create(APIServices::class.java)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        stepView = findViewById(R.id.step_view)
        stepView.go(1, true)
        setupUserDetails()
        orderSummaryRV.layoutManager = LinearLayoutManager(this)
        getAllCart()
        placeOrder.setOnClickListener {
            if (i == 0) {
                Util.showToast(this, "Please select payment medium", 1)

            } else if (i == 1) {
                startUPIPayment()
            } else if (i == 2) {
                Util.startLoading(dialog)
                var finalOrder= Order(
                    generateOrderItem(), orderID,"N/A","N/A",
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString(),
                    finalPrice.roundToInt(),
                    Util.user!!.companyName, Util.user!!.address, getPaymentStatus()
                )
                createOrder(finalOrder)
            }
        }

    }

    fun startUPIPayment(){
        val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"

        val uri: Uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", "hscharpreet@upi")
            .appendQueryParameter("pn", "Harpreet Singh")
            // .appendQueryParameter("mc", "your-merchant-code")
            .appendQueryParameter("tn", "For Order(${orderID}) at Pankaj Oil")
            .appendQueryParameter("tr", orderID)
            .appendQueryParameter("am", finalPrice.roundToInt().toString())
            .appendQueryParameter("cu", "INR")
            //  .appendQueryParameter("url", "your-transaction-url")
            .build()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
//        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
//        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE)

        val intentChooser = Intent.createChooser(intent,"Pay with")
        if (null != intentChooser.resolveActivity(packageManager)){
            Util.startLoading(dialog)
            startActivityForResult(intentChooser, UPI_PAYMENT)
        }else{
            Util.showToast(this, "No UPI app found,please install one to continue", 1)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPI_PAYMENT){
            if (Activity.RESULT_OK == resultCode ||(resultCode == 11)){
                if (data !=null){
                    val trxt = data.getStringExtra("response")
                    Log.e("UPI", "onActivityResult: $trxt")
                    val dataList: ArrayList<String> = ArrayList()
                    dataList.add(trxt)
                    upiPaymentDataOperation(dataList)
                }else{
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    Util.stopLoading(dialog)
                }
            }else if (resultCode != Activity.RESULT_OK){
                Util.stopLoading(dialog)
            }
            else{
                Log.e("UPI", "onActivityResult: " + "Return data is null");
                Util.stopLoading(dialog)
            }
        }
    }
    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        var str = data[0]
        var remove = str.split("&")
        var hashMap : HashMap<String, String>
                = HashMap<String, String> ()
        for (i in remove.indices){
            var temp = remove[i].split("=")
            hashMap.put(temp[0],temp[1])
        }
        if (hashMap["Status"] == "SUCCESS"){
            Toast.makeText(this,"Payment Done",Toast.LENGTH_SHORT).show()
            Util.showToast(this, "Payment Done", 0)


            var fOrder= Order(
                generateOrderItem(), orderID,hashMap["ApprovalRefNo"].toString(),hashMap["txnId"].toString(),
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString(),
                finalPrice.roundToInt(),
                Util.user!!.companyName, Util.user!!.address, getPaymentStatus()
            )
            createOrder(fOrder)
        }else{
            Util.showToast(this, "Payment Failed", 0)

            Util.stopLoading(dialog)
        }
    }
    //create order
    fun createOrder(order: Order) {

        val call = service.addOrder(
            order,
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey()
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Util.stopLoading(dialog)
                Util.showToast(this@OrderSummary, "Order Not Saved",1)

            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                var tempList = Util.user!!.orders as ArrayList<Order>
                tempList.add(order)
                Util.user!!.orders = tempList
                Handler().postDelayed({
                    Util.stopLoading(dialog)
                    DroidDialog.Builder(this@OrderSummary)
                        .icon(R.drawable.ic_action_tick)
                        .title("Order Placed")
                        .color(R.color.colorPrimaryDark, Color.WHITE, Color.WHITE)
                        .content("You will be contacted on your registered number for delivery updates.")
                        .cancelable(true, false)
                        .positiveButton(
                            "Ok"
                        ) {
                            startActivity(Intent(this@OrderSummary, MainActivity::class.java))
                            finish()
                        }
                        .show()
                }, 1000)
            }

        })
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
            id.text = "Order : "+orderID

        }
    }

    public fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
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
        price.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(prodPrice)}"
        igst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(igstPrice)}"
        cst.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(cstPrice)}"
        final.text =
            "₹ ${NumberFormat.getNumberInstance(Locale.US).format(finalPrice.roundToInt())}"
    }

    fun generateOrderItem(): List<Item> {
        var itemList: ArrayList<Item> = ArrayList<Item>()
        for (i in list) {
            itemList.add(Item(i.productName, i.size, i.quantity, i.url, i.amount))
        }
        return itemList
    }
    fun getPaymentStatus(): String {
        if (i == 1) {
            return "online"
        } else {
            return "cash"
        }
    }
    fun generateOrderID(): String {
        val n = 100000 + Random().nextInt(900000)
        return n.toString()
    }

    fun getAllCart(){
        Util.startLoading(dialog)
        val call = service.getAllCart(
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey()
        )
        call.enqueue( object :Callback<List<OrderEntity>>{
            override fun onFailure(call: Call<List<OrderEntity>>, t: Throwable) {
                Util.stopLoading(dialog)
            }

            override fun onResponse(call: Call<List<OrderEntity>>, response: Response<List<OrderEntity>>) {
                Util.stopLoading(dialog)
                list = response.body()!!
                computeProductPrice(list)
                orderSummaryRV.adapter = SummaryCartAdapter(list)

            }

        })
    }


    //Add Here





}
