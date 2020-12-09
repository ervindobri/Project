package com.example.project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.project.helpers.ArrayConverter

@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters( ArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}