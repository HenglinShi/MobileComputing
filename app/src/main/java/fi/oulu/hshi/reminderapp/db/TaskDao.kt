package fi.oulu.hshi.reminderapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TaskDao {
    @Transaction
    @Insert
    fun insert(taskInfo: TaskInfo): Long

    @Query("DELETE FROM taskInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM taskInfo")
    fun getTaskInfos(): List<TaskInfo>




}