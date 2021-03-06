package fi.oulu.hshi.reminderapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "userInfo")
data class UserInfo(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name="username") var username:String,
    @ColumnInfo(name="email")  var email:String,
    @ColumnInfo(name="password") var password:String
): Serializable