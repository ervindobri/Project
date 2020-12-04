package com.example.project.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoints {

    @GET("restaurants?country=MX")
    fun getRestaurants(@Query("page") page : Int): Call<ResponseData>

    @GET("restaurants")
    fun filterRestaurants(@Query("country") country: String,
                          @Query("price") price: Int?,
                          @Query("address") address: String?,
                          @Query("city") city: String?,
                          @Query("zip") zipCode: Int?,
                          @Query("page") page : Int
    ) : Call<ResponseData>
}
