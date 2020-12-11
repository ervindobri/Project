package com.example.project.vmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.project.database.UserDatabase
import com.example.project.models.RestaurantData
import com.example.project.models.RestaurantRepository
import com.example.project.database.RestaurantDatabase
import com.example.project.database.User
import com.example.project.database.UserUpdate
import com.example.project.models.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var currentUser : LiveData<User>
    private val repository: RestaurantRepository
    private val userRepo: UserRepository
    lateinit var favoritesLive : LiveData<List<RestaurantData>>
    lateinit var favorites : List<RestaurantData>

    init {
        val restaurantDAO = RestaurantDatabase.getDatabase(application).restaurantDao()
        val userDAO = UserDatabase.getDatabase(application).userDao()
        repository = RestaurantRepository(restaurantDAO)
        userRepo = UserRepository(userDAO)
        favoritesLive = repository.favoritesLive
        currentUser = userRepo.getUser(1)
    }

    fun updateUser(obj: UserUpdate){
        viewModelScope.launch {
            userRepo.updateUser(obj)
        }
    }

    fun closeDB() {
        viewModelScope.launch {
            UserDatabase.closeDB()
        }
    }

}