package com.example.project.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val baseURL = "https://ratpark-api.imok.space/"

    val interceptor = HttpLoggingInterceptor()
    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: ApiEndpoints by lazy {
        retrofit.create(ApiEndpoints::class.java)
    }
}