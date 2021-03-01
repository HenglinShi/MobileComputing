package com.example.homework1

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.homework1.databinding.ActivityMainBinding
import com.example.homework1.db.AppDatabase
import com.example.homework1.db.ReminderInfo
import java.util.concurrent.Executor
import kotlin.random.Random
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit


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
            startActivity(
                Intent(applicationContext, CreateReminderActivity::class.java)
            )
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


        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _->

            val selectedTask = listView.adapter.getItem(position) as ReminderInfo
            val message =
                "Do you want to delete this reminder?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete task?")
                .setMessage(message)
                .setNegativeButton("Delete") { _, _ ->
                    val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
                    db.reminderDao().delete(selectedTask.rid!!)

                    cancelReminder(applicationContext, selectedTask.rid!!)
                    refreshListView()
                }

                .setPositiveButton("Modify"){ _, _ ->
                    var intent = Intent(this@MainActivity, ModifyReminderActivity::class.java)
                    intent.putExtra("reminder", selectedTask.rid)
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
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            val reminders = db.reminderDao().getReminders()
            if (reminders.isNotEmpty()) {
                val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                listView.adapter = adaptor
            } else {
                listView.adapter = null
                Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun refreshListView(begDate:Int, endDate:Int) {

        var refreshTask = LoadTaskInfoEntries()
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))


            var selectedDate = SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH)//.format(Calendar.getInstance().time)
            val reminders = db.reminderDao().getReminders().toMutableList()

            var now = Calendar.getInstance().time//selectedDate.format(Calendar.getInstance().time)

            if (reminders.isNotEmpty()){
                var i = 0
                while (i < reminders.size) {
                    var stime = reminders[i].reminder_time
                    var dtime = selectedDate.parse(stime)

                    if (dtime.before(now)) {
                        reminders.removeAt(i)
                    }
                    else {
                        i += 1
                    }

                }
            }



            if (reminders.isNotEmpty()) {


                val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                listView.adapter = adaptor
            } else {
                listView.adapter = null
                Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun refreshListViewPast() {

        var refreshTask = LoadTaskInfoEntries()
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))


            var selectedDate = SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH)//.format(Calendar.getInstance().time)
            val reminders = db.reminderDao().getReminders().toMutableList()

            var now = Calendar.getInstance().time//selectedDate.format(Calendar.getInstance().time)

            if (reminders.isNotEmpty()){
                var i = 0
                while (i < reminders.size) {
                    var stime = reminders[i].reminder_time
                    var dtime = selectedDate.parse(stime)

                    if (dtime.after(now)) {
                        reminders.removeAt(i)
                    }
                    else {
                        i += 1
                    }

                }
            }



            if (reminders.isNotEmpty()) {


                val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                listView.adapter = adaptor
            } else {
                listView.adapter = null
                Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
            }
        }
    }


    inner class LoadTaskInfoEntries: Executor{
        override fun execute(command: Runnable?) {
            command?.run()
        }

    }


    companion object {


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
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.reminder_options, menu)
        return true

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.menu_main_all_reminder -> {

            refreshListView()
            true
        }

        R.id.menu_main_past_reminder -> {
            refreshListViewPast()
                true
        }

        R.id.menu_main_future_reminder -> {
            refreshListView(0,0)
            true

        }
        R.id.menu_main_locationed_reminder -> {
            refreshListViewWithLoc()
            true
        }
        R.id.menu_main_notLocationed_reminder -> {
            refreshListViewNoLoc()
            true
        }
        R.id.menu_main_duedated_reminder -> {
                true
        }
        R.id.menu_main_noDuedated_reminder -> {
                true

        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun refreshListViewWithLoc() {

        var refreshTask = LoadTaskInfoEntries()
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))
            val reminders = db.reminderDao().getRemindersHasLoc()

            if (reminders.isNotEmpty()) {


                val adaptor = TaskInfoAdaptor(applicationContext, reminders)
                listView.adapter = adaptor
            } else {
                listView.adapter = null
                Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshListViewNoLoc() {

        var refreshTask = LoadTaskInfoEntries()
        refreshTask.execute{
            val db = AppDatabase.getDatabase(applicationContext, getString(R.string.dbFileName))

            val reminders = db.reminderDao().getRemindersNoLoc()

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