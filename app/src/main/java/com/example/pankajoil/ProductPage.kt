package com.example.pankajoil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.pankajoil.`interface`.OnSelectionMade
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_page.*

class ProductPage : AppCompatActivity(), OnSelectionMade {
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

    override fun onVariantSelected(index: String) {

        updateUI(product!!.variants[index.toInt()])

    }

    fun updateUI(variant: Variant){
        Picasso.get().load(variant.url).into(image)
        size.text = "${variant.size} ℓ"
        quantity.text = "${variant.perCarton} piece"
        item_price.text = "₹ ${variant.price}"

        Handler().postDelayed({
            bottomSheet.dismiss()
        }, 1000)


    }

}
