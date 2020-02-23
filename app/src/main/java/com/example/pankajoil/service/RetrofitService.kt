package com.example.pankajoil.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.pankajoil.utils.Util
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService{

    companion object {
        fun retrofitService(ctx: Context): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(getConfiguredRetrofitClient(ctx).build())
                .baseUrl("https://pankaj-oil-api.herokuapp.com/").build()
        }


        private fun getConfiguredRetrofitClient(ctx: Context): OkHttpClient.Builder {
            val myCache = Cache(ctx.cacheDir, Util.cacheSize)
            return OkHttpClient.Builder()
                .cache(myCache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (hasNetwork(ctx)!!)
                        request.newBuilder().header(
                            "Cache-Control",
                            "public, max-age=" + 60 * 60 * 24 * 5
                        ).build()
                    else
                        request.newBuilder().header(
                            "Cache-Control",
                            "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 10
                        ).build()
                    chain.proceed(request)
                }
                .connectTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)

        }

        private fun hasNetwork(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }
    }
}