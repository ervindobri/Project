package com.example.project.models

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.project.api.RetrofitClient
import com.example.project.database.RestaurantDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantRepository(private val dao: RestaurantDao) {
    val favoritesLive: LiveData<List<RestaurantData>> = dao.getFavoritesLive()


    //Fetching restaurants async
     suspend fun getAll(country: String, name: String, price: Int?, address: String, city: String, zipCode: String, page: Int?) : ResponseData {
        try {
            val response = RetrofitClient.api.filterRestaurants(country,name,price,address,city,zipCode,page)
            response.restaurants.forEach {
                val found  = dao.findByID(it.id)
                it.images.add(it.image_url)
                if ( found != null ){
                    //its present in the favorites
                    it.favorite = found.favorite
                    it.images.addAll(found.images)
                    it.images = it.images.distinct() as ArrayList<String>

                    // display new image from local db
                    it.image_url = found.images[found.images.size-1]
                }
            }
            return response
        }
        catch (e: Exception){
            Log.e("@@@ Retrofit-Exception", e.message.toString())
            return ResponseData(0,0,0, arrayListOf())
        }
    }

    //Finding specific restaurant
    suspend fun findRestaurant(id : Long) : RestaurantData? = withContext(Dispatchers.IO){
        return@withContext dao.findByID(id)

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