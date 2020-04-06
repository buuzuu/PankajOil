package com.example.pankajoil

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pankajoil.bottomSheets.VariantsBottomSheet
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.User
import com.example.pankajoil.data.WishlistProducts
import com.example.pankajoil.roomDatabase.OrderEntity
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkEventListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_product_page.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProductPage : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var addToCart: FloatingActionButton
    var product: Product? = null
    private lateinit var dialog: android.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_page)
        toolbar = findViewById(R.id.toolbar)
        setupID()
        dialog = SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom)
            .setMessage("Adding...").setCancelable(false).build()
        setSupportActionBar(toolbar)
        addToCart = findViewById(R.id.addToCart)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        product = intent.getSerializableExtra("product") as Product
        Util.prod_BottomSheet = VariantsBottomSheet(product!!)
        Util.current_Variant = product!!.variants[0]

        Picasso.get().load(Util.current_Variant!!.url).into(Util.prod_Image)
        Util.prod_Size!!.text = "${Util.current_Variant!!.size} ℓ"
        Util.prod_Quantity!!.text = "${Util.current_Variant!!.perCarton} piece"
        Util.prod_Item_Name!!.text = product!!.productName
        Util.prod_Item_Price!!.text = "₹ ${Util.current_Variant!!.price}"
        Util.prod_About!!.text = product!!.description


        volume.setOnClickListener {
            Util.prod_BottomSheet!!.show(supportFragmentManager, "VariantBottomSheet")
        }
        //Setting wishlist
        val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
        val call = service.addToWishList(
            WishlistProducts(product!!.uniqueID, product!!.productName, product!!.generalUrl)
            ,
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey()
        )
        val call2 = service.deleteFromWishList(
            TokenSharedPreference(this).getMobileNumber(),
            TokenSharedPreference(this).getAuthKey(),
            product!!.uniqueID
        )
        spark_button.setEventListener(object : SparkEventListener {
            override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {
            }

            override fun onEvent(button: ImageView?, buttonState: Boolean) {
                if (buttonState) {
                    if (TokenSharedPreference(this@ProductPage).isTokenPresent()) {
                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(this@ProductPage, t.message, Toast.LENGTH_LONG)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                when (response.code()) {
                                    200 -> {
                                        Toast.makeText(
                                            this@ProductPage,
                                            "Added",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val products: ArrayList<WishlistProducts> =
                                            Util.user!!.wishlistProducts as ArrayList<WishlistProducts>
                                        products.add(
                                            WishlistProducts(
                                                product!!.uniqueID,
                                                product!!.productName,
                                                product!!.generalUrl
                                            )
                                        )
                                        Util.user!!.wishlistProducts = products

                                    }
                                    400 -> {
                                        Toast.makeText(this@ProductPage, "404", Toast.LENGTH_SHORT)
                                            .show()

                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@ProductPage,
                                            response.code(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }

                            }

                        })

                    } else {
                        Snackbar.make(viewMore, "Please Sign in your account", Snackbar.LENGTH_LONG)
                            .show()
                    }
                } else if (!buttonState) {
                    if (TokenSharedPreference(this@ProductPage).isTokenPresent()) {
                        call2.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(this@ProductPage, t.message, Toast.LENGTH_LONG)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                when (response.code()) {
                                    200 -> {
                                        Toast.makeText(
                                            this@ProductPage,
                                            "Removed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val products: ArrayList<WishlistProducts> =
                                            Util.user!!.wishlistProducts as ArrayList<WishlistProducts>
                                        products.forEachIndexed { index, wishlistProducts ->
                                            if (wishlistProducts.uniqueID == product!!.uniqueID) {
                                                products.removeAt(index)
                                            }
                                        }
                                        Util.user!!.wishlistProducts = products


                                    }
                                    400 -> {
                                        Toast.makeText(this@ProductPage, "404", Toast.LENGTH_SHORT)
                                            .show()

                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@ProductPage,
                                            response.code(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            }

                        })
                    } else {
                        Snackbar.make(viewMore, "Please Sign in your account", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }

            override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {
            }

        })



        addToCart.setOnClickListener {

            val call3 = service.addToCart(
                TokenSharedPreference(this).getMobileNumber(),
                TokenSharedPreference(this).getAuthKey(),
                OrderEntity(
                    product!!.uniqueID,
                    product!!.productName,
                    Util.current_Variant!!.size,
                    1,
                    Util.current_Variant!!.url,
                    Util.current_Variant!!.price,
                    Util.current_Variant!!.perCarton
                )

            )
            addToCart(
                call3, OrderEntity(
                    product!!.uniqueID,
                    product!!.productName,
                    Util.current_Variant!!.size,
                    1,
                    Util.current_Variant!!.url,
                    Util.current_Variant!!.price,
                    Util.current_Variant!!.perCarton
                )
            )

        }

    }

    fun setupID() {
        Util.prod_Image = findViewById(R.id.image)
        Util.prod_About = findViewById(R.id.about)
        Util.prod_Item_Price = findViewById(R.id.item_price)
        Util.prod_Item_Name = findViewById(R.id.item_name)
        Util.prod_Size = findViewById(R.id.size)
        Util.prod_Quantity = findViewById(R.id.quantity)
    }

    override fun onStart() {
        super.onStart()
        if (TokenSharedPreference(this@ProductPage).isTokenPresent()) {
            setSparkButton(Util.user!!, product!!.uniqueID)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, Cart::class.java))

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


    private fun setSparkButton(user: User, id: String) {
        var check = false
        val products = user.wishlistProducts
        for (p in products) {
            if (p.uniqueID == id) {
                check = true
            }
        }
        if (check) {
            spark_button.isChecked = check
        }
    }

    fun addToCart(
        call3: Call<ResponseBody>,
        orderEntity: OrderEntity
    ) {
        Util.startLoading(dialog)


        call3.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductPage, "Retry", Toast.LENGTH_SHORT).show()
                Util.stopLoading(dialog)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Util.stopLoading(dialog)
                if (response.code() == 409){
                    startActivity(Intent(this@ProductPage, Cart::class.java))
                }else if (response.code() == 200){
                    val cart: ArrayList<OrderEntity> =
                        Util.user!!.cartItems
                    cart.add(orderEntity)
                    Util.user!!.cartItems = cart
                    startActivity(Intent(this@ProductPage, Cart::class.java))
                }else{
                    Toast.makeText(this@ProductPage, "Something went wrong", Toast.LENGTH_SHORT).show()

                }



            }

        })

    }

}
