package com.example.homework1

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.homework1.db.AppDatabase
import com.example.homework1.databinding.ActivityLoginBinding
import com.example.homework1.db.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    //var currentUsername: String = null
    //var currentPassword: String = null
    var userInfos = listOf<UserInfo>()
    //private var uid by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            Log.d("Lab", "Register Button Clicked")
            startActivity(
                Intent(applicationContext, RegistrationActivity::class.java)
            )
        }

        binding.btnLogin.setOnClickListener{
            Log.d("Lab", "Login Button Clicked")

            applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE).edit().putInt("LoginStatus", 1).apply()

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
                    var currentUserName = binding.txtUsername.text.toString()
                    userInfos = db.userDao().getUserInfos(username = currentUserName)
                }
            }


                // todo check if username exists




            if (userInfos.isNotEmpty()) {
                if (userInfos[0].password== binding.txtPassword.text.toString()) {
                    //login success

                    applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE).edit().putString("currentUsername", userInfos[0].username).apply()


                    startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                    )

                }
                else{
                    Toast.makeText(applicationContext, "password incorrect", Toast.LENGTH_SHORT).show()

                }


            }else {
                Toast.makeText(applicationContext, "user does not exist", Toast.LENGTH_SHORT).show()
            }

        }
        //checkLoginStatus()
    }

    override fun onResume() {
        super.onResume()
        //checkLoginStatus()
    }


    private fun checkLoginStatus() {

        val loginStatus = applicationContext.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1){
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

    }

}