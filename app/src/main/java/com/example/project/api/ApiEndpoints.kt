package com.example.project.api

import com.example.project.models.ResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoints {


    @GET("restaurants")
    suspend fun filterRestaurants(@Query("country") country: String,
                                  @Query("name") name: String?,
                                  @Query("price") price: Int?,
                                  @Query("address") address: String?,
                                  @Query("city") city: String?,
                                  @Query("zip") zipCode: String?,
                                  @Query("page") page : Int?
    ) : ResponseData
}