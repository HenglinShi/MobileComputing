package fi.oulu.hshi.reminderapp


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fi.oulu.hshi.reminderapp.db.AppDatabase
import fi.oulu.hshi.reminderapp.databinding.ActivityLoginBinding
import fi.oulu.hshi.reminderapp.db.UserInfo
import com.google.gson.Gson
import fi.oulu.hshi.reminderapp.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    lateinit var currentUser: User

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


            var currentUserName = binding.txtUsername.text.toString()
            database = Firebase.database.reference
            database.child("users").child(currentUserName).get().addOnSuccessListener {
                currentUser = it.getValue(User::class.java)!!
                if (binding.txtUsername.text.toString() == currentUser.password){
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
                else {
                    Log.e("Login", "incorrect pass word")
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

        }
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