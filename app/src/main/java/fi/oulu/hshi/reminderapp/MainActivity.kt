package fi.oulu.hshi.reminderapp


import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.room.Room
import fi.oulu.hshi.reminderapp.databinding.ActivityMainBinding
import fi.oulu.hshi.reminderapp.db.AppDatabase
import fi.oulu.hshi.reminderapp.db.ReminderInfo
import java.util.concurrent.Executor
import kotlin.random.Random
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.database.*
import fi.oulu.hshi.reminderapp.entity.Reminder
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit




class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var binding: ActivityMainBinding
    lateinit var db: DatabaseReference
    lateinit var adaptor: TaskInfoAdaptor
    var reminders: MutableList<Reminder>? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseDatabase.getInstance().reference.child("reminders")


        listView = binding.taskListView
        reminders = mutableListOf()

        adaptor = TaskInfoAdaptor(applicationContext, reminders!!)
        listView.adapter = adaptor


        var _taskListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadTaskList(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
            }
        }

        db.orderByKey().addValueEventListener(_taskListener)




        binding.btnAddNew.setOnClickListener {
            startActivity(
                Intent(applicationContext, CreateReminderActivity::class.java))

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

        binding.btnMapView.setOnClickListener {
            startActivity(
                Intent(applicationContext, MapsActivity2::class.java)
            )


        }




        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _->

            val selectedTask = listView.adapter.getItem(position) as Reminder
            val message = "Do you want to delete this reminder?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete task?").setMessage(message).setNegativeButton("Delete") { _, _ ->

                    var item = db.child(selectedTask.key)
                    item.removeValue()

                }

                .setPositiveButton("Modify"){ _, _ ->
                    var intent = Intent(this@MainActivity, CreateReminderActivity::class.java)
                    intent.putExtra("reminderToModifyKey", selectedTask.key)
                    startActivity(intent)
                }

                .setNeutralButton("Cancel") { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }
                .show()
        }
    }



    private fun loadTaskList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadTaskList")

        val items = dataSnapshot.children.iterator()
        reminders!!.clear()
        while (items.hasNext()) {
            val item = items.next()
            var reminder = item.getValue(Reminder::class.java)

            if (reminder != null) {
                reminders!!.add(reminder)
            }
        }

        adaptor.notifyDataSetChanged()
    }




    companion object {
        fun setReminderWithWorkManager(context: Context, reminderId: Int, timeInMillis: Long, message: String) {

            val reminderParameters = Data.Builder()
                .putString("message", message)
                .putInt("uid", reminderId)
                .build()

            // get minutes from now until reminder
            var minutesFromNow = 0L
            if (timeInMillis > System.currentTimeMillis())
                minutesFromNow = timeInMillis - System.currentTimeMillis()

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(reminderParameters)
                .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(reminderRequest)
        }




        fun setReminder(context: Context, reminderId: Int, timeInMills: Long, message: String) {

            val intent = Intent(context, ReminderReciever::class.java)

            intent.putExtra("reminderId", reminderId)
            intent.putExtra("message", message)

            val pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_ONE_SHOT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC, timeInMills, pendingIntent)

        }


        fun cancelReminder(context: Context, pendingIntentId: Int) {
            val intent = Intent(context, ReminderReciever::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, PendingIntent.FLAG_ONE_SHOT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        fun showNotification(context: Context, message: String) {

            val CHANNEL_ID = "BANKING_APP_NOTIFICATION_CHANNEL"
            var notificationId = Random.nextInt(10, 1000) + 5
            // notificationId += Random(notificationId).nextInt(1, 500)

            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_account_box)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(CHANNEL_ID)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Notification chancel needed since Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())

        }

    }

}