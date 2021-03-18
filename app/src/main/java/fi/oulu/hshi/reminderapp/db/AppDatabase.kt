package fi.oulu.hshi.reminderapp.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fi.oulu.hshi.reminderapp.R


@Database(entities = arrayOf(
   UserInfo::class,
   TaskInfo::class,
   ReminderInfo::class), version = 1)

abstract class AppDatabase:RoomDatabase() {
   abstract fun userDao():UserDao
   abstract fun taskDao():TaskDao
   abstract fun reminderDao(): ReminderDao


}



