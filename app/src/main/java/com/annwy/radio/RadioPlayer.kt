package com.annwy.radio

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_radio_player.*
import java.util.*

class RadioPlayer : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var radioUrl: String
    private lateinit var radioLabel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            radioUrl = it.getString(RADIO_STATION_URL)
            radioLabel = it.getString(RADIO_STATION_LABEL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_radio_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        play_button.setOnClickListener { playMedia(); changeEnabledButton() }
        pause_button.setOnClickListener { pauseMedia(); changeEnabledButton() }
        stop_schedule_button.setOnClickListener { onScheduleStopButton() }
        radio_label.text = radioLabel
    }

    private fun onScheduleStopButton() {
        val time = stop_time.text.split(":")
        val hour = time[0].toInt()
        val minutes = time[1].toInt()
        scheduleStopService(context!!, hour, minutes)
    }

    private fun changeEnabledButton() {
        play_button.isEnabled = !play_button.isEnabled
        pause_button.isEnabled = !pause_button.isEnabled
        stop_schedule_button.isEnabled = !stop_schedule_button.isEnabled
        stop_time.isEnabled = !stop_time.isEnabled
    }

    private fun playMedia() {
        val playerIntent = Intent(context, MediaPlayerService::class.java)
        playerIntent.putExtra("radioUrl", radioUrl)
        playerIntent.action = MediaPlayerService.ACTION_PLAY
        context?.startService(playerIntent)
    }

    private fun pauseMedia() {
        val playerIntent = Intent(context, MediaPlayerService::class.java)
        playerIntent.action = MediaPlayerService.ACTION_STOP
        context?.startService(playerIntent)
    }

    private fun scheduleStopService(mContext: Context, hour: Int, minute: Int) {
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mContext, MediaPlayerService::class.java)
        intent.action = MediaPlayerService.ACTION_STOP
        val pendingIntent = PendingIntent.getService(mContext, 0, intent, 0)

        // reset previous pending intent
        alarmManager.cancel(pendingIntent)

        // Set the alarm to start at approximately 08:00 morning.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // if the scheduler date is passed, move scheduler time to tomorrow
        if (System.currentTimeMillis() > calendar.timeInMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }

    companion object {
        const val RADIO_STATION_URL = "com.radio.annwy.radio.station_url"
        const val RADIO_STATION_LABEL = "com.radio.annwy.radio.station_label"

        @JvmStatic
        fun newInstance(bundle: Bundle) = RadioPlayer().apply {
            arguments = Bundle().apply { putAll(bundle) }
        }
    }
}
