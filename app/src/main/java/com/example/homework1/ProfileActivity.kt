package com.example.homework1

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.example.homework1.databinding.ActivityProfileBinding
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.UserInfo
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    var currentUserInfo = listOf<UserInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_profile)


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




        if (currentUserInfo.isNotEmpty()) {


            binding.txtUsername.setText(currentUserInfo[0].username)
            binding.txtEmailaddress.setText((currentUserInfo[0].email))

        }

        val view = binding.root
        setContentView(view)

        binding.btnReturn.setOnClickListener {
            //TODO TO THE LOGOUT ACITVITY
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }



        binding.btnUpdate.setOnClickListener {
            //TODO TO THE LOGOUT ACITVITY

            val userInfo = UserInfo(
                uid = currentUserInfo[0].uid,
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
                val uuid = db.userDao().updateUser(userInfo)//.toInt()
                db.close()


                // Need to pop out account create success
                // Check if username existed
            }
            finish()


            Toast.makeText(applicationContext, "Userinfo updated!", Toast.LENGTH_SHORT).show()


            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }
    }

    override fun onResume(){
        super.onResume()
        if (currentUserInfo.isNotEmpty()){


            binding.txtUsername.setText(currentUserInfo[0].username)
            binding.txtEmailaddress.setText((currentUserInfo[0].email))

        }

        val view = binding.root
        setContentView(view)
    }





}