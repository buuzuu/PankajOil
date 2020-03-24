package com.example.pankajoil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import retrofit2.Callback
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pankajoil.`interface`.OnVariantDataSent
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.example.pankajoil.data.WishlistProducts
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkEventListener
import kotlinx.android.synthetic.main.activity_product_page.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ProductPage : AppCompatActivity(), OnVariantDataSent {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomSheet: VariantsBottomSheet
    var product: Product?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_page)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        product = intent.getSerializableExtra("product") as Product
        Log.d("TAG", product!!.productName)
        bottomSheet = VariantsBottomSheet(product!!, this)
        Picasso.get().load(product!!.variants[0].url).into(image)
        size.text = "${product!!.variants[0].size} ℓ"
        quantity.text = "${product!!.variants[0].perCarton} piece"
        item_name.text = product!!.productName
        item_price.text = "₹ ${product!!.variants[0].price}"
        about.text = product!!.description
        volume.setOnClickListener {
            bottomSheet.show(supportFragmentManager, "VariantBottomSheet")
        }

        //Setting wishlist
        val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
        val call = service.addToWishList(WishlistProducts(product!!.uniqueID,product!!.productName,product!!.generalUrl)
            ,TokenSharedPreference(this).getMobileNumber(), TokenSharedPreference(this).getAuthKey())
        spark_button.setEventListener(object : SparkEventListener {
            override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {
            }

            override fun onEvent(button: ImageView?, buttonState: Boolean) {
                if (buttonState){
                    if (TokenSharedPreference(this@ProductPage).isTokenPresent()){
                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(this@ProductPage,t.message,Toast.LENGTH_LONG).show()

                            }
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                when (response.code()) {
                                    200 -> {
                                        Toast.makeText(this@ProductPage,"Added",Toast.LENGTH_SHORT).show()
                                    }
                                    400 -> {
                                        Toast.makeText(this@ProductPage,"404",Toast.LENGTH_SHORT).show()

                                    }else -> {
                                    Toast.makeText(this@ProductPage,response.code(),Toast.LENGTH_SHORT).show()

                                }
                                }

                            }

                        })

                    }else{
                        Snackbar.make(viewMore,"Please Sign in your account",Snackbar.LENGTH_LONG).show()
                    }
                }else{

                }
            }

            override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {

                true
            }
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateUI(variant: Variant?){
        Picasso.get().load(variant!!.url).into(image)
        size.text = "${variant.size} ℓ"
        quantity.text = "${variant.perCarton} piece"
        item_price.text = "₹ ${variant.price}"

        Handler().postDelayed({
            bottomSheet.dismiss()
        }, 500)

    }

    override fun onRecieveVariant(variant: Variant) {
        Log.d("TAG",variant!!.price.toString() + "From Product page")
        updateUI(variant)
    }

}
