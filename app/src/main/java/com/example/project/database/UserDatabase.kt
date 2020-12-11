package com.example.project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.project.helpers.ArrayConverter

@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters( ArrayConverter::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao


    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun closeDB(){
            synchronized(this){
                INSTANCE?.close()
            }
        }

        fun getDatabase(context: Context): UserDatabase {
            val temInstance = INSTANCE
            if(null != temInstance) {
                return temInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "users"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}