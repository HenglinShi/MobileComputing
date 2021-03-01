package com.example.homework1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReciever :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderId = intent?.getIntExtra("reminderId", 0)
        val text = intent?.getStringExtra("message")

        MainActivity.showNotification(context!!, text!!)
    }
}