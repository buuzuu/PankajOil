package com.example.pankajoil.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.ProductPage
import com.example.pankajoil.R
import com.example.pankajoil.TokenSharedPreference
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.WishlistProducts
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.squareup.picasso.Picasso
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkEventListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.ArrayList

class WishlistGridAdapter(var list: ArrayList<WishlistProducts>, var ctx: Context) :
    RecyclerView.Adapter<WishlistGridAdapter.WishViewHolder>() {

    val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WishlistGridAdapter.WishViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.wishlist_grid_item_layout, null)
        return WishViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WishlistGridAdapter.WishViewHolder, position: Int) {

        holder.textView.text = list[position].productName
        Picasso.get().load(list[position].generalUrl).into(holder.imageView)
        holder.spark.isChecked = true
        holder.spark.setEventListener(object : SparkEventListener {
            override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {
            }

            override fun onEvent(button: ImageView?, buttonState: Boolean) {
                if (!buttonState) {
                    val call2 = service.deleteFromWishList(
                        TokenSharedPreference(ctx).getMobileNumber(),
                        TokenSharedPreference(ctx).getAuthKey(),
                        list[position].uniqueID
                    )
                    removeFromWishlist(call2, list[position].uniqueID)
                    notifyItemChanged(position)
                    notifyDataSetChanged()
                    list.removeAt(position)

                }
            }

            override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {
            }

        })

    }

    inner class WishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView = itemView.findViewById(R.id.item_image)
        var textView: TextView = itemView.findViewById(R.id.item_text)
        var spark: SparkButton = itemView.findViewById(R.id.spark_button)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val product = findProduct(list[adapterPosition].uniqueID)
            val intent = Intent(ctx, ProductPage::class.java)
            intent.putExtra("product", product as Serializable)
            ctx.startActivity(intent)

        }

        fun findProduct(s: String): Product? {
            val products: ArrayList<Product> = Util.products as ArrayList<Product>
            products.forEachIndexed { index, product ->
                if (product.uniqueID == s) {
                    return product
                }
            }
            return null
        }

    }

    fun removeFromWishlist(call2: Call<ResponseBody>, s: String) {
        call2.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(ctx, t.message, Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when (response.code()) {
                    200 -> {
                        Toast.makeText(
                            ctx,
                            "Removed",
                            Toast.LENGTH_LONG
                        ).show()
                        val products: ArrayList<WishlistProducts> =
                            Util.user!!.wishlistProducts as ArrayList<WishlistProducts>
                        products.forEachIndexed { index, wishlistProducts ->
                            if (wishlistProducts.uniqueID == s) {
                                products.removeAt(index)
                            }
                        }
                        Util.user!!.wishlistProducts = products
                    }
                    400 -> {
                        Toast.makeText(ctx, "404", Toast.LENGTH_SHORT)
                            .show()

                    }
                    else -> {
                        Toast.makeText(
                            ctx,
                            response.code(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

        })

    }

}