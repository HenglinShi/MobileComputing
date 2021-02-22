package com.example.homework1

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.homework1.db.AppDatabase
import com.example.homework1.databinding.ActivityCreateReminderBinding
import com.example.homework1.db.ReminderInfo
import com.example.homework1.db.TaskInfo
import com.example.homework1.db.UserInfo
import com.google.gson.Gson
import java.time.LocalDateTime
import java.util.*

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateReminderBinding

    val REQUEST_CODE = 100



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_create_reminder)


        binding = ActivityCreateReminderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnReturn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        //binding.reminderImage.setClick
        binding.reminderImageCreate.setOnClickListener{

            val builder = AlertDialog.Builder(this@CreateReminderActivity)
            builder.setTitle("Add image")
                .setNegativeButton("Gallery") { _, _ ->
                    var galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    galleryIntent.setType("image/*")
                    startActivityForResult(galleryIntent, REQUEST_CODE)

                }
                .setPositiveButton("Camera"){ _, _ ->

                }
                .show()










            return@setOnClickListener
        }
        binding.btnAccept.setOnClickListener {
            // Adding new reminder

            if (binding.txtDate.text.isEmpty()) {

                return@setOnClickListener
            }

            val gson = Gson()
            val json = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE).getString("currentUser", "")

            var currentUser = gson.fromJson(json, UserInfo::class.java)








            // put the new item to the database
            // TODO CREATING THE NEW TASK OBJECT

            val reminder = ReminderInfo(
                null,
                //taskname = binding.txtTaskName.text.toString(),
                //taskdesc = binding.txtTaskDesc.text.toString(),
                //duedate = binding.txtDate.text.toString()
                message = binding.txtTaskDesc.text.toString(),
                creator_id = currentUser.uid,
                location_x = binding.txtLocationX.text.toString().toDouble(),
                location_y = binding.txtLocationY.text.toString().toDouble(),
                reminder_see = false,

                creation_time = Calendar.getInstance().toString(),
                reminder_time = binding.txtDate.text.toString()
            )


            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            db.reminderDao().insert(reminder).toInt()
            db.close()


            Toast.makeText(
                applicationContext,
                "New task added.",
                Toast.LENGTH_SHORT
            ).show()
            finish()

        }






    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            binding.reminderImageCreate.setImageURI(data?.data) // handle chosen image
        }
    }


}