package com.example.homework1

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.UserInfo
import com.example.homework1.databinding.ActivityRegistrationBinding

import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btnConfirm.setOnClickListener {
            //validate entry values here
            if (binding.txtUsername.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Username should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (binding.txtEmailaddress.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Email address should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (binding.txtPassword.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Password should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val userInfo = UserInfo(
                null,
                username = binding.txtUsername.text.toString(),
                email = binding.txtEmailaddress.text.toString(),
                password = binding.txtPassword.text.toString()
            )



            AsyncTask.execute {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
                val uuid = db.userDao().insert(userInfo).toInt()
                db.close()


                // Need to pop out account create success
                // Check if username existed
            }
            finish()
        }
    }
}