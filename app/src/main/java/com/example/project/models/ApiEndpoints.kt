package com.example.project.models

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoints {

    @GET("restaurants?country=MX")
    fun getRestaurants(@Query("page") page : Int): Call<ResponseData>


}
