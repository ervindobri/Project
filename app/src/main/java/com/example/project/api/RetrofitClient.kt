package com.example.project.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://opentable.herokuapp.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: ApiEndpoints by lazy {
        retrofit.create(ApiEndpoints::class.java)
    }
}