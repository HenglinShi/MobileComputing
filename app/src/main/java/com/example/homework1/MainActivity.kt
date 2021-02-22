package com.example.homework1

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.homework1.databinding.ActivityMainBinding
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.ReminderInfo

import com.example.homework1.db.TaskInfo

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
            //TODO to the taks creation activity
            startActivity(
                Intent(applicationContext, CreateReminderActivity::class.java)
            )

        }

        binding.btnLogout.setOnClickListener {
            //TODO TO THE LOGOUT ACITVITY
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
            )
        }

        binding.btnProfile.setOnClickListener {
            //TODO TO THE Profile ACITVITY
            startActivity(
                Intent(applicationContext, ProfileActivity::class.java)
            )
        }


        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, id->

            // TODO task in detail
            // TODO MAYBE DO DELETION

            val selectedTask = listView.adapter.getItem(position) as ReminderInfo
            val message =
                "Do you want to delete this reminder?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete task?")
                .setMessage(message)
                .setNegativeButton("Delete") { _, _ ->
                    // Update UI

                    //delete from database
                    AsyncTask.execute {
                        val db = Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            ).build()

                        db.reminderDao().delete(selectedTask.rid!!)
                    }


                    //refresh payments list
                    refreshListView()
                }
                .setPositiveButton("Modify"){ _, _ ->
                    var intent = Intent(this@MainActivity, ModifyReminderActivity::class.java)
                    //var bundle = Bundle()
                    intent.putExtra("reminder", selectedTask)
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
        refreshTask.execute()
    }

    inner class LoadTaskInfoEntries: AsyncTask<String?, String?, List<ReminderInfo>>(){
        override fun doInBackground(vararg params: String?) :List<ReminderInfo> {
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))


            val reminders = db.reminderDao().getReminders()
            db.close()
            return reminders

        }

        override fun onPostExecute(reminders: List<ReminderInfo>?) {
            super.onPostExecute(reminders)
            if (reminders != null) {
                if (reminders.isNotEmpty()) {
                    val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                    listView.adapter = adaptor
                } else {
                    listView.adapter = null
                    Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
                }
            }
        }




    }



}