package com.basis.net


import com.basis.base.utils.LogUtils
import com.basis.net.utils.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitManager {

    companion object{
        val instance:RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

    var hashMap:HashMap<String,Retrofit> = HashMap()

    fun getRetrofit(url:String): Retrofit? {
        var retrofit = hashMap.get(url)
        if (retrofit==null){
            val addInterceptor = OkHttpClient.Builder().addInterceptor {
                return@addInterceptor it.proceed(
                    it.request().newBuilder()
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Device-Type", "Android")
                        .build()
                )
            }
           val loggingInterceptor= HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
               message?.let {
                   LogUtils.d("RetrofitLog", it)
               }
           })
            loggingInterceptor.level=HttpLoggingInterceptor.Level.BODY
            addInterceptor.addInterceptor(loggingInterceptor)
            addInterceptor.connectTimeout(NetConstant.timeOut,TimeUnit.SECONDS).readTimeout(NetConstant.timeOut,TimeUnit.SECONDS).writeTimeout(NetConstant.timeOut,TimeUnit.SECONDS).build()
            retrofit=Retrofit.Builder().client(addInterceptor.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build()
            hashMap?.put(url,retrofit)
        }
        return retrofit
    }
}