package fi.oulu.hshi.reminderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import fi.oulu.hshi.reminderapp.db.AppDatabase
import fi.oulu.hshi.reminderapp.db.UserInfo
import fi.oulu.hshi.reminderapp.databinding.ActivityRegistrationBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fi.oulu.hshi.reminderapp.entity.User
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

            val database = Firebase.database
            val reference = database.getReference("users")
            //val key = reference.push().key
            //if (key != null) {
            val user = User(binding.txtUsername.text.toString(), binding.txtEmailaddress.text.toString(), binding.txtPassword.text.toString())
            //reference.setValue(user)
            reference.child(user.username).setValue(user)
            //}


            finish()
        }
    }
}