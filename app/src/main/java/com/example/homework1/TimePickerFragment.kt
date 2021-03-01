package com.example.homework1

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        //var selectedDate = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(c.getTime());
        val c = Calendar.getInstance()



        var tt = activity?.findViewById<TextView>(R.id.txtDate)
        var dateString = tt?.text.toString().split('/')
        //dateString = dateString.split("/").toString()

        c.set(Calendar.YEAR, dateString[2].toInt());
        c.set(Calendar.MONTH, dateString[0].toInt()-1);
        c.set(Calendar.DAY_OF_MONTH, dateString[1].toInt());
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        var selectedDate = SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.ENGLISH).format(c.getTime());
        //var tt = activity?.findViewById<TextView>(R.id.txtDate)
        if (tt != null) {
            tt.setText(selectedDate)
        }

    }
}