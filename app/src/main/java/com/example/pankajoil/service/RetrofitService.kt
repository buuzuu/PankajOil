package com.example.pankajoil.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.example.pankajoil.utils.Util
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService{


    companion object {
        fun retrofitService(ctx: Context): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
              //  .client(getConfiguredRetrofitClient(ctx).build())
                .client(okHttpClient(ctx))
                .baseUrl("https://pankaj-oil-api.herokuapp.com/").build()
        }

        private fun okHttpClient(ctx: Context):OkHttpClient{
            val myCache = Cache(ctx.cacheDir, Util.cacheSize)
            return  OkHttpClient.Builder()
                .cache(myCache)
                .addInterceptor(httpLoggingInterceptor())
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offLineInterceptor(ctx))
                .build()
        }
        private fun httpLoggingInterceptor():HttpLoggingInterceptor{
            val interceptor= HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Log.d("TAG",message) })
            interceptor.level = HttpLoggingInterceptor.Level.BODY
                        return  interceptor
        }

        private fun networkInterceptor():Interceptor{
            return Interceptor { chain ->
                Log.d("TAG", "network interceptor: called.");
                val response :Response = chain.proceed(chain.request())
                val cacheControl:CacheControl = CacheControl.Builder().maxAge(1,TimeUnit.DAYS).build()

                response.newBuilder().removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .build()
            }
        }


        private fun offLineInterceptor(ctx:Context):Interceptor{
            return Interceptor { chain ->
                Log.d("TAG", "offline interceptor: called.");
                var request:Request = chain.request()

                if (!hasNetwork(ctx)!!){
                    val cacheControl:CacheControl = CacheControl.Builder().maxStale(3,TimeUnit.DAYS).build()
                    request = request.newBuilder().removeHeader("Pragma")
                        .removeHeader("Cache-Control").cacheControl(cacheControl).build()

                }
                chain.proceed(request)

            }
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
                            "public, max-age=" + 172800
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