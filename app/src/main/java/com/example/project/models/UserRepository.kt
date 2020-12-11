package com.example.project.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.project.database.UserDatabase
import com.example.project.database.User
import com.example.project.database.UserDao
import com.example.project.database.UserUpdate


class UserRepository(private val userDAO : UserDao) {
    private val DB_NAME = "users"

    fun insertUser(
        firstName: String?,
        lastName: String?,
        emailAddress: String?,
        address: String?,
        picture: String?,
        phone: String?
    ) {
        val user = User(
            getMaxID(),
            firstName,
            lastName,
            emailAddress,
            address,
            picture,
            phone
        )
        insertUser(user)
    }

    suspend fun updateUser(obj: UserUpdate){
        userDAO.updateUser(obj)
    }
    fun insertUser(user: User) {
        val thread  = Thread{
            userDAO.insertAll(user)
        }
        thread.start()
    }

    fun getMaxID(): Int{
        return userDAO.getMaxID()
    }

    fun getUser(id: Int): LiveData<User> {
        return userDAO.getUser(id)
    }

    val users: List<User>
        get() = userDAO.getAll()

}