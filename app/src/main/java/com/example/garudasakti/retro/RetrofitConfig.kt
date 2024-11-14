package com.example.garudasakti.retro

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitConfig {
    // http://192.168.76.3:8000/api/
    // http://10.0.2.2:8000/api/
    // http://127.0.0.1:8000/api/
    // http://192.168.187.65:8000/api/
    private val BASE_URL = "https://121f-103-108-22-31.ngrok-free.app/api/" // sesuaikan BASE_URL
    fun getRetrofitClientInstance():Retrofit{

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}