package com.example.homework1

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.example.homework1.db.AppDatabase
import com.example.homework1.databinding.ActivityCreateReminderBinding
import com.example.homework1.db.ReminderInfo
import com.example.homework1.db.TaskInfo
import com.example.homework1.db.UserInfo
import java.time.LocalDateTime
import java.util.*

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateReminderBinding

    var currentUserInfo = listOf<UserInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_create_reminder)


        binding = ActivityCreateReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnReturn.setOnClickListener {
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )

        }
        binding.btnAccept.setOnClickListener {
            // Adding new reminder

            if (binding.txtTaskName.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Task bane should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (binding.txtDate.text.isEmpty()) {

                return@setOnClickListener
            }

            val currentUsername = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
            ).getString("currentUsername", " ").toString()


            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName)
            ).allowMainThreadQueries().build()


            currentUserInfo = db.userDao().getUserInfos(username = currentUsername)
            db.close()



            // put the new item to the database
            // TODO CREATING THE NEW TASK OBJECT

            val reminder = ReminderInfo(
                null,
                //taskname = binding.txtTaskName.text.toString(),
                //taskdesc = binding.txtTaskDesc.text.toString(),
                //duedate = binding.txtDate.text.toString()
                message = binding.txtTaskDesc.text.toString(),
                creator_id = currentUserInfo[0].uid,
                location_x = binding.txtLocationX.text.toString().toDouble(),
                location_y = binding.txtLocationY.text.toString().toDouble(),
                reminder_see = false,

                creation_time = Calendar.getInstance().toString(),
                reminder_time = binding.txtDate.text.toString()
            )


            AsyncTask.execute {
                //save payment to room datbase
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
                // TODO
                // TODO THE DATA INSERT
                val uuid = db.reminderDao().insert(reminder).toInt()
                db.close()

            }

            Toast.makeText(
                applicationContext,
                "New task added.",
                Toast.LENGTH_SHORT
            ).show()
            finish()

        }






    }


}