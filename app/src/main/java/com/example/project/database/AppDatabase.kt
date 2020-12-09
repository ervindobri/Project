package com.example.project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.project.helpers.Converters

@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters( Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}