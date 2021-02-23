package com.example.homework1

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text
import java.util.*


class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

    public var DATE_SET = 202
    @RequiresApi(Build.VERSION_CODES.N)
    val c = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        var selectedDate = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(c.getTime());

        var tt = activity?.findViewById<TextView>(R.id.txtDate)
        if (tt != null) {
            tt.setText(selectedDate)
        }
        val newFragment = TimePickerFragment()
        activity?.supportFragmentManager?.let { newFragment.show(it, "timePicker") }

    }





}