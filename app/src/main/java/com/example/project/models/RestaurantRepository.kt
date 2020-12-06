package com.example.project.models

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.project.api.RetrofitClient
import com.example.project.database.RestaurantDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantRepository(private val dao: RestaurantDao) {
    val favoritesLive: LiveData<List<RestaurantData>> = dao.getFavoritesLive()
//    val favorites: List<RestaurantData> = dao.getFavorites()
//    val restaurants: ArrayList<RestaurantData> = getAll().restaurants


     suspend fun getAll(country: String, name: String, price: Int?, address: String, city: String, zipCode: String, page: Int?) : ResponseData {
        try {
            Log.d("stuff", "country:$country $name $price $address $city $zipCode $page")
            val response = RetrofitClient.api.filterRestaurants(country,name,price,address,city,zipCode,page)
            response.restaurants.forEach {
                val found  = dao.findByID(it.id)
                if ( found != null ){
//                    Log.d("found!", found.id.toString())
                    //its present in the favorites
                    it.favorite = found.favorite
                }
            }
            return response
        }
        catch (e: Exception){
            Log.i("ex", e.message.toString())
            return ResponseData(0,0,0, arrayListOf())
        }
    }

    suspend fun insert(restaurant: RestaurantData) {
        dao.insertRestaurant(restaurant)
    }

    fun delete(restaurant: RestaurantData) {
        dao.delete(restaurant)
    }

}