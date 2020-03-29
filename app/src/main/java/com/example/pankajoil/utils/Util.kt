package com.example.pankajoil.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pankajoil.bottomSheets.PasswordResetBottomSheet
import com.example.pankajoil.R
import com.example.pankajoil.bottomSheets.RegisterBottomSheet
import com.example.pankajoil.TokenSharedPreference
import com.example.pankajoil.bottomSheets.VariantsBottomSheet
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.User
import com.example.pankajoil.data.Variant
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class Util {

    companion object {

        //Product Page variables
        var current_Variant:Variant?=null

        var prod_Image:ImageView?= null
        var prod_Size:TextView?= null
        var prod_Quantity:TextView?= null
        var prod_Item_Name:TextView?= null
        var prod_Item_Price:TextView?= null
        var prod_About:TextView?= null
        var prod_BottomSheet:VariantsBottomSheet?= null

        //Cart
        var cartItem: TextView?=null
        var qty: TextView?=null
        var empty_Image: ImageView?=null
        lateinit var cart_Bottom:FloatingActionButton





        var signin_text: TextView? = null
        var orsymbol_text: TextView? = null
        var register: TextView? = null
        var header_name: TextView? = null
        var header_mobile: TextView? = null
        var profilePicture: CircleImageView? = null
        var user: User? = null
        var products: List<Product>? = null
        var variant:Variant?=null
        const val cacheSize = (5 * 1024 * 1024).toLong()
        val passwordSheet =
            PasswordResetBottomSheet()
        val registerSheet =
            RegisterBottomSheet()

        val generalRetrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient())
            .baseUrl("https://pankaj-oil-api.herokuapp.com/")

            .build()!!

        private fun okHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        fun setupLoggedInOrOutView(
            header_user: TextView,
            header_mobile: TextView,
            signin_text: TextView,
            register_text: TextView,
            orsymbol_text: TextView,
            ctx: Context
        ) {

            if (TokenSharedPreference(ctx).isTokenPresent()) {

                signin_text.visibility = View.GONE
                orsymbol_text.visibility = View.GONE
                register_text.visibility = View.GONE
                header_user.visibility = View.VISIBLE
                header_mobile.visibility = View.VISIBLE

            } else if (!TokenSharedPreference(ctx).isTokenPresent()) {
                header_user.visibility = View.GONE
                header_mobile.visibility = View.GONE
                signin_text.visibility = View.VISIBLE
                register_text.visibility = View.VISIBLE
                orsymbol_text.visibility = View.VISIBLE
            }

        }

        fun signOut(
            header_user: TextView,
            header_mobile: TextView,
            signin_text: TextView,
            register_text: TextView,
            orsymbol_text: TextView,
            ctx: Context
        ) {
            TokenSharedPreference(ctx).deleteAuthKey()
            setupLoggedInOrOutView(
                header_user,
                header_mobile,
                signin_text,
                register_text,
                orsymbol_text,
                ctx
            )
            profilePicture!!.setImageResource(R.drawable.test_image)


        }


        fun startLoading(dialog: android.app.AlertDialog) {
            dialog.show()

        }

        fun stopLoading(dialog: android.app.AlertDialog) {
            dialog.dismiss()
        }

    }
}