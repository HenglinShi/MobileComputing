package fi.oulu.hshi.reminderapp.db

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

    @Query("SELECT * FROM reminderInfo WHERE rid = :id")
    fun getReminder(id: Int): ReminderInfo

    @Query("SELECT * FROM reminderInfo")
    fun getReminders(): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE (creation_time >= :today)")
    fun getRemindersAfterToday(today:String): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE (creation_time <= :today)")
    fun getRemindersBeforeToday(today:String): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE (reminder_see == :tt)")
    fun getRemindersReminded(tt:Boolean): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE (location_x is not null)")
    fun getRemindersHasLoc(): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE (location_x is null)")
    fun getRemindersNoLoc(): List<ReminderInfo>

    @Update
    fun updateReminder(reminderInfo: ReminderInfo) : Int

}