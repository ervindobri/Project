package com.example.project.vmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.project.models.RestaurantData
import com.example.project.models.RestaurantRepository
import com.example.project.database.RestaurantDatabase
import com.example.project.database.User

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    var currentUser : User? = null
    private val repository: RestaurantRepository
    lateinit var favoritesLive : LiveData<List<RestaurantData>>
    lateinit var favorites : List<RestaurantData>

    init {
        val dao = RestaurantDatabase.getDatabase(application).restaurantDao()
        repository = RestaurantRepository(dao)
        favoritesLive = repository.favoritesLive
//        favorites = repository.favorites
    }

}