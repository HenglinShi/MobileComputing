package fi.oulu.hshi.reminderapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "taskInfo")
data class TaskInfo(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name="taskname") var taskname:String,
    @ColumnInfo(name="taskdesc")  var taskdesc:String,
    @ColumnInfo(name="duedate") var duedate:String
)