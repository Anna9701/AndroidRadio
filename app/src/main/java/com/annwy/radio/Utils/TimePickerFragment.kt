package com.annwy.radio.Utils

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.TimePicker
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var timeText: EditText

    fun setTimeEditText(editText: EditText) : TimePickerFragment {
        timeText = editText
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val hours = if (p1 < 10) "0$p1" else p1.toString()
        val minutes = if (p2 < 10) "0$p2" else p2.toString()
        val time = "$hours:$minutes"
        timeText.text = time.toEditable()
    }

    companion object {
        val TIME_EDIT_TEXT_KEY = "com.annwy.radio.radio_player.time_edit_text"
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}