package com.example.homework1.db

import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = arrayOf(
   UserInfo::class,
   TaskInfo::class), version = 1)

abstract class AppDatabase:RoomDatabase() {
   abstract fun userDao():UserDao
   abstract fun taskDao():TaskDao
}



