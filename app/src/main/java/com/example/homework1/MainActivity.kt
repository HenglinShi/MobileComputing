package com.example.homework1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.homework1.databinding.ActivityMainBinding
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.ReminderInfo
import com.example.homework1.db.UserInfo
import com.google.gson.Gson
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        listView = binding.taskListView

        refreshListView()

        binding.btnAddNew.setOnClickListener {
            startActivity(
                Intent(applicationContext, CreateReminderActivity::class.java)
            )
        }

        binding.btnLogout.setOnClickListener {
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
            )
        }

        binding.btnProfile.setOnClickListener {
            startActivity(
                Intent(applicationContext, ProfileActivity::class.java)
            )
        }


        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _->

            val selectedTask = listView.adapter.getItem(position) as ReminderInfo
            val message =
                "Do you want to delete this reminder?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete task?")
                .setMessage(message)
                .setNegativeButton("Delete") { _, _ ->
                    val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
                    db.reminderDao().delete(selectedTask.rid!!)

                    refreshListView()
                }

                .setPositiveButton("Modify"){ _, _ ->
                    var intent = Intent(this@MainActivity, ModifyReminderActivity::class.java)
                    intent.putExtra("reminder", selectedTask.rid)
                    startActivity(intent)
                }

                .setNeutralButton("Cancel") { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }
                .show()


        }
    }

    override fun onResume() {
        super.onResume()
        refreshListView()
    }

    private fun refreshListView() {

        var refreshTask = LoadTaskInfoEntries()
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            val reminders = db.reminderDao().getReminders()
            if (reminders.isNotEmpty()) {
                val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                listView.adapter = adaptor
            } else {
                listView.adapter = null
                Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class LoadTaskInfoEntries: Executor{
        override fun execute(command: Runnable?) {
            command?.run()
        }

    }
}