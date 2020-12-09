package com.example.project.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project.models.RestaurantData
import com.example.project.models.RestaurantUpdate


@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun getAll(): LiveData<List<RestaurantData>>

    @Query("SELECT * FROM restaurants WHERE id IN (:resID)")
    suspend fun findByID(resID: Int): RestaurantData?


    @Delete
    fun delete(restaurant: RestaurantData)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantData)


    @Query("SELECT * FROM restaurants WHERE favorite = 1 ORDER BY NAME ASC")
    fun getFavoritesLive() : LiveData<List<RestaurantData>>

//    @Query("SELECT * FROM restaurants WHERE favorite = 1 ORDER BY NAME ASC")
//    fun getFavorites() : List<RestaurantData>

//    @Query("UPDATE restaurants SET images=:images WHERE id = :rid")
    @Update(entity = RestaurantData::class)
    suspend fun updateRestaurant(obj: RestaurantUpdate)
}