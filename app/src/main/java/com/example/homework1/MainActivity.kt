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

            val selectedTask = listView.adapter.getItem(position) as TaskInfo
            val message =
                "Do you want to delete ${selectedTask.taskname} payment, on ${selectedTask.duedate}?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete task?")
                .setMessage(message)
                .setPositiveButton("Delete") { _, _ ->
                    // Update UI

                    //delete from database
                    AsyncTask.execute {
                        val db = Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            ).build()

                        db.taskDao().delete(selectedTask.uid!!)
                    }


                    //refresh payments list
                    refreshListView()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
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

    inner class LoadTaskInfoEntries: AsyncTask<String?, String?, List<TaskInfo>>(){
        override fun doInBackground(vararg params: String?) :List<TaskInfo> {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName)
            ).build()

            val taskInfos = db.taskDao().getTaskInfos()
            db.close()
            return taskInfos

        }

        override fun onPostExecute(taskInfos: List<TaskInfo>?) {
            super.onPostExecute(taskInfos)
            if (taskInfos != null) {
                if (taskInfos.isNotEmpty()) {
                    val adaptor = TaskInfoAdaptor(applicationContext, taskInfos)
                    listView.adapter = adaptor
                } else {
                    listView.adapter = null
                    Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
                }
            }
        }




    }



}