package com.example.homework1

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.homework1.databinding.ActivityModifyReminderBinding
import com.example.homework1.databinding.ActivityProfileBinding
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.ReminderInfo

class ModifyReminderActivity : AppCompatActivity() {

    private lateinit var binding:ActivityModifyReminderBinding
    private lateinit var reminder:ReminderInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyReminderBinding.inflate(layoutInflater)

        var intent = getIntent()
        reminder = intent.getSerializableExtra("reminder") as ReminderInfo

        binding.txtTaskDesc.setText(reminder.message)
        binding.txtCreatedDate.setText(reminder.creation_time)
        binding.txtDate.setText(reminder.reminder_time)
        binding.txtLocationX.setText(reminder.location_x.toString())
        binding.txtLocationY.setText(reminder.location_y.toString())

        val view = binding.root
        setContentView(view)



        binding.btnAccept.setOnClickListener {
            val remind2Update = ReminderInfo(
                rid = reminder.rid,
                message = binding.txtTaskDesc.text.toString(),
                creator_id = reminder.creator_id,
                location_x = binding.txtLocationX.text.toString().toDouble(),
                location_y = binding.txtLocationY.text.toString().toDouble(),
                reminder_see = reminder.reminder_see,
                creation_time = binding.txtCreatedDate.text.toString(),
                reminder_time = binding.txtDate.text.toString()
            )
            AsyncTask.execute {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
                val uuid = db.reminderDao().updateReminder(remind2Update)//.toInt()
                db.close()


                // Need to pop out account create success
                // Check if username existed
            }
            finish()
        }

        binding.btnReturn.setOnClickListener {
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }

    }

    override fun onResume() {
        super.onResume()
        binding.txtTaskDesc.setText(reminder.message)
        binding.txtCreatedDate.setText(reminder.creation_time)
        binding.txtDate.setText(reminder.reminder_time)
        binding.txtLocationX.setText(reminder.location_x.toString())
        binding.txtLocationY.setText(reminder.location_y.toString())

        val view = binding.root
        setContentView(view)
    }

}