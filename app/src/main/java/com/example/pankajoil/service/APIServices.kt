package com.example.pankajoil.service

import com.example.pankajoil.data.LoginCredentials
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.SignupUser
import com.example.pankajoil.data.User
import com.example.pankajoil.utils.Util
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit


interface APIServices {

    @GET("products")
    fun getProducts(): Call<List<Product>>

    @POST("auth")
    fun signIn(@Body credentials: LoginCredentials): Call<JSONObject>

    @GET("users/{id}")
    fun getUserDetails(  @Path("id") id: String , @Header("x-Auth-Token") authKey:String): Call<User>


    @POST("resetPassword")
    fun changePassword(@Body credentials: LoginCredentials ):Call<ResponseBody>

    @POST("users")
    fun createUser( @Body user:SignupUser):Call<ResponseBody>




}


