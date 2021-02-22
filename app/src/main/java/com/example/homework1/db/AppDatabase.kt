package com.example.homework1.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.homework1.R


@Database(entities = arrayOf(
   UserInfo::class,
   TaskInfo::class,
   ReminderInfo::class), version = 1)

abstract class AppDatabase:RoomDatabase() {
   abstract fun userDao():UserDao
   abstract fun taskDao():TaskDao
   abstract fun reminderDao(): ReminderDao


   companion object {
      @Volatile private var INSTANCE: AppDatabase? = null


      fun getDatabase(context: Context, dbFileName: String) : AppDatabase{
         val tmpInstance = INSTANCE
         if (tmpInstance != null){
            return tmpInstance
         }
         synchronized(this) {
            val instance = Room.databaseBuilder(context,
                     AppDatabase::class.java,
                     dbFileName).build()

            INSTANCE = instance
            return instance
         }
      }
   }
}



