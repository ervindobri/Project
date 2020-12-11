package com.example.project.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userId)")
    fun getUser(userId: Int): LiveData<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT MAX(uid)+1 from user")
    fun getMaxID(): Int

    @Update(entity = User::class)
    suspend fun updateUser(obj: UserUpdate)
}