package com.example.project.models

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.project.api.RetrofitClient
import com.example.project.database.RestaurantDao

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
                    Log.d("found!", found.images.size.toString())
                    //its present in the favorites
                    it.favorite = found.favorite
                    it.images = found.images
                    // last image means we added it there :)
                    it.image_url = found.images[found.images.size-1]
                }
                else{
                    it.image_url = it.images[0]
                }
            }
            return response
        }
        catch (e: Exception){
            Log.i("retrofit-ex", e.message.toString())
            return ResponseData(0,0,0, arrayListOf())
        }
    }

    suspend fun update(obj : RestaurantUpdate){
        dao.updateRestaurant(obj)
    }

    suspend fun insert(restaurant: RestaurantData) {
        dao.insertRestaurant(restaurant)
    }

    fun delete(restaurant: RestaurantData) {
        dao.delete(restaurant)
    }

}