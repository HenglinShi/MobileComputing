package com.example.homework1.db

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reminderInfo")
data class ReminderInfo(
    @PrimaryKey(autoGenerate = true) var rid: Int?,
    @ColumnInfo(name="message") var message:String,
    @ColumnInfo(name="location_x") var location_x:Double,
    @ColumnInfo(name="location_y") var location_y:Double,
    @ColumnInfo(name="creator_id") var creator_id:Int?,
    @ColumnInfo(name="reminder_see") var reminder_see:Boolean,
    @ColumnInfo(name="reminder_time") var reminder_time:String,
    @ColumnInfo(name="creation_time") var creation_time:String,
    @ColumnInfo(name="image_uri") var image_uri: String
):Serializable