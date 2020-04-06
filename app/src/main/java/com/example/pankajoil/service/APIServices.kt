package com.example.pankajoil.service

import com.example.pankajoil.data.*
import com.example.pankajoil.roomDatabase.OrderEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Body


interface APIServices {

    @GET("products")
    fun getProducts(): Call<List<Product>>

    @POST("auth")
    fun signIn(@Body credentials: LoginCredentials): Call<JSONObject>

    @GET("users/{id}")
    fun getUserDetails(@Path("id") id: String, @Header("x-Auth-Token") authKey: String): Call<User>


    @POST("resetPassword")
    fun changePassword(@Body credentials: LoginCredentials): Call<ResponseBody>

    @PUT("users/addOrder/{id}")
    fun addOrder(@Body order: Order,@Path("id") id: String, @Header("x-Auth-Token") authKey: String):Call<ResponseBody>

    @POST("paytm/generate_checksum")
    fun getPaytmChecksum(@Body payObj:PaytmPostObject):Call<Checksum>

    @POST("paytm/transaction")
    fun getPaytmTxnToken(@Body payObj:PaytmPostObject):Call<TxnToken>

    @POST("payumoney/generate_checksum")
    fun getPayumoneyChecksum(@Body payumoney:PayumoneyPostObject): Call<Checksum>


    @POST("users")
    fun createUser(@Body user: SignupUser): Call<ResponseBody>

    @PUT("users/addToWishlist/{id}")
    fun addToWishList(@Body wishlistProducts: WishlistProducts,@Path("id") id: String, @Header("x-Auth-Token") authKey: String):Call<ResponseBody>

    @DELETE("users/deleteWishListProduct/{id}/{uniqueID}")
    fun deleteFromWishList(@Path("id") id: String,@Header("x-Auth-Token") authKey: String,@Path("uniqueID") u_id: String):Call<ResponseBody>


    @Multipart
    @POST("uploadProfileImage/{id}")
    fun uploadProfileImage(@Path("id") id: String, @Part file: MultipartBody.Part, @Header("x-Auth-Token") authKey: String): Call<ProfileImage>


    @POST("users/addToCart/{id}")
    fun addToCart(@Path("id") id: String,@Header("x-Auth-Token") authKey: String, @Body orderEntity: OrderEntity): Call<ResponseBody>

    @DELETE("users/deleteCartItem/{id}/{size}/{uniqueID}")
    fun deleteCartItem(@Path("id") id: String, @Header("x-Auth-Token") authKey: String, @Path("size") size: Float, @Path("uniqueID") uniqueID: String): Call<ResponseBody>

    @GET("users/getAllCart/{id}")
    fun getAllCart(@Path("id") id: String,@Header("x-Auth-Token") authKey: String): Call<List<OrderEntity>>


    @PUT("users/updateCart/{id}")
    fun updateCartItem(@Path("id") id: String,@Header("x-Auth-Token") authKey: String,@Body updateCart: UpdateCart):Call<ResponseBody>





}


