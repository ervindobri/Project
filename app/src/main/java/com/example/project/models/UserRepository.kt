package com.example.project.models

import android.content.Context
import androidx.room.Room
import com.example.project.database.AppDatabase
import com.example.project.database.User


class UserRepository(context: Context) {
    private val DB_NAME = "users"
    private val userDatabase: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

    fun insertUser(
        firstName: String?,
        lastName: String?,
        emailAddress: String?,
        address: String?,
        picture: ByteArray?,
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

    fun insertUser(user: User) {
        val thread  = Thread{
            userDatabase.userDao().insertAll(user)
        }
        thread.start()
    }

    fun getMaxID(): Int{
        return userDatabase.userDao().getMaxID()
    }

    fun getUser(id: Int): User {
        return userDatabase.userDao().getUser(id)
    }

    val users: List<User>
        get() = userDatabase.userDao().getAll()

}