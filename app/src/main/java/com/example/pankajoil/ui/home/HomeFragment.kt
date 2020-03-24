package com.example.jetpack_kotlin.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.ProductPage
import com.example.pankajoil.R
import com.example.pankajoil.SearchActivity
import com.example.pankajoil.`interface`.OnGridItemClickListner
import com.example.pankajoil.`interface`.OnHorizontalGridItemClickListner
import com.example.pankajoil.`interface`.OnSliderItemClickListner
import com.example.pankajoil.adapter.GridAdapter
import com.example.pankajoil.adapter.HorizontalGridAdapter
import com.example.pankajoil.adapter.SliderAdapter
import com.example.pankajoil.data.Product
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.service.RetrofitService
import com.example.pankajoil.utils.Util
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class HomeFragment : Fragment() {

    var slide: SliderView? = null
    private lateinit var dialog: android.app.AlertDialog
    lateinit var intent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        slide = root.findViewById(R.id.imageSlider)
        intent = Intent(activity!!.applicationContext, ProductPage::class.java)
        root.viewMore.setOnClickListener {
            startActivity(Intent(activity!!.applicationContext, SearchActivity::class.java))
        }
        root.viewMore2.setOnClickListener {
            startActivity(Intent(activity!!.applicationContext, SearchActivity::class.java))
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog = SpotsDialog.Builder().setContext(activity).setTheme(R.style.Custom)
            .setMessage("Loading...").setCancelable(false).build()
        setupProducts()

    }


    private fun setupProducts() {

        dialog.show()
        val service: APIServices =
            RetrofitService.retrofitService(activity!!.applicationContext)
                .create(APIServices::class.java)
        val call = service.getProducts()
        call.enqueue(object : Callback<List<Product>>, OnGridItemClickListner,
            OnHorizontalGridItemClickListner, OnSliderItemClickListner {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(activity!!.applicationContext,"Display Something", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(data: Call<List<Product>>, response: Response<List<Product>>) {

                if (response.code() == 200) {
                    dialog.dismiss()
                    Util.products = response.body()!!
                    // First data set
                    val adapter = SliderAdapter(
                        activity!!.applicationContext,
                        Util.products!!.subList(0, 5),
                        this
                    )
                    slide!!.setSliderAdapter(adapter)
                    slide!!.startAutoCycle()
                    slide!!.setIndicatorAnimation(IndicatorAnimations.WORM)
                    slide!!.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    grid_rv.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)
                    horizontal_grid_rv.layoutManager =
                        GridLayoutManager(activity!!.applicationContext, 2)
                    grid_rv2.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)
                    // Second data set
                    grid_rv.adapter = GridAdapter(
                        activity!!.applicationContext,
                        Util.products!!.subList(5, 9),
                        this
                    )
                    // Third data set
                    setupBanner(Util.products!![9])

                    //Fourth Data set
                    horizontal_grid_rv.adapter = HorizontalGridAdapter(
                        activity!!.applicationContext,
                        Util.products!!.subList(16, 18),
                        this
                    )
                    //Fifth data set
                    setupBanner2(Util.products!![10])
                    //sixth data set
                    grid_rv2.adapter = HorizontalGridAdapter(
                        activity!!.applicationContext,
                        Util.products!!.subList(12, 16),
                        this
                    )

                    // seventh data set
                    setPartImages(
                        Util.products!![11],
                        Util.products!![18],
                        Util.products!![19]
                    )


                }
            }

            override fun onGridItemClick(item: Product, position: Int) {
                intent.putExtra("product", item as Serializable)
                startActivity(intent)
            }

            override fun onHorizontalGridItemClick(item: Product, position: Int) {
                intent.putExtra("product", item as Serializable)
                startActivity(intent)

            }

            override fun onSliderItemClick(item: Product, position: Int) {
                intent.putExtra("product", item as Serializable)
                startActivity(intent)
            }

        })


    }

    private fun setupBanner(product: Product) {
        Picasso.get().load(product.generalUrl).into(banner)
        banner.setOnClickListener {
            intent.putExtra("product", product as Serializable)
            startActivity(intent)
        }
    }

    private fun setupBanner2(product: Product) {
        Picasso.get().load(product.generalUrl).into(banner2)
        banner2.setOnClickListener {
            intent.putExtra("product", product as Serializable)
            startActivity(intent)
        }
    }

    private fun setPartImages(product1: Product, product2: Product, product3: Product) {
        Picasso.get().load(product1.generalUrl).into(part1)
        Picasso.get().load(product2.generalUrl).into(part2)
        Picasso.get().load(product3.generalUrl).into(part3)
        part1.setOnClickListener {
            intent.putExtra("product", product1 as Serializable)
            startActivity(intent)
        }
        part2.setOnClickListener {
            intent.putExtra("product", product2 as Serializable)
            startActivity(intent)
        }
        part3.setOnClickListener {
            intent.putExtra("product", product3 as Serializable)
            startActivity(intent)
        }

    }

}