package com.example.homework1.db

import androidx.room.*

@Dao
interface ReminderDao {
    @Transaction
    @Insert
    fun insert(reminderInfo: ReminderInfo): Long

    @Query("DELETE FROM reminderInfo WHERE rid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminderInfo")
    fun getReminder(): ReminderInfo

    @Query("SELECT * FROM reminderInfo")
    fun getReminders(): List<ReminderInfo>

    @Update
    fun updateReminder(reminderInfo: ReminderInfo) : Int

}