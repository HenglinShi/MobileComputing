package fi.oulu.hshi.reminderapp


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import fi.oulu.hshi.reminderapp.databinding.ActivityProfileBinding
import fi.oulu.hshi.reminderapp.db.AppDatabase
import fi.oulu.hshi.reminderapp.db.UserInfo
import com.google.gson.Gson
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    var currentUserInfo = listOf<UserInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_profile)

        val mgson = Gson()
        val json = applicationContext.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE).getString("currentUser", "")

        var currentUser = mgson.fromJson(json, UserInfo::class.java)


        binding.txtUsername.setText(currentUser.username)
        binding.txtEmailaddress.setText((currentUser.email))


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



            val db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName))
                .allowMainThreadQueries().
                fallbackToDestructiveMigration().build()
            //AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            val uuid = db.userDao().updateUser(userInfo)
            db.close()
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