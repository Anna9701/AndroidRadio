package com.annwy.radio

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.annwy.radio.Utils.TimePickerFragment
import kotlinx.android.synthetic.main.fragment_radio_player.*
import java.util.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.support.design.widget.Snackbar
import android.widget.ImageButton
import com.annwy.radio.models.RadioStation

class RadioPlayer() : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var radioStation: RadioStation
    private var scheduledStopServiceIntent: PendingIntent? = null
    private var isPlaying = false
    private var currentPlayerServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            radioStation = it.getParcelable(RADIO_STATION)
            currentPlayerServiceIntent = it.getParcelable(PLAYER_SERVICE_INTENT)
        }
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_radio_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListeners()
        disableButtonsOnInitialization()
        radio_label.text = radioStation.radioName
    }

    private fun disableButtonsOnInitialization() {
        setImageButtonEnabled(context!!, false, pause_button, android.R.drawable.ic_media_pause)
        setImageButtonEnabled(context!!, false, sleep_button, android.R.drawable.ic_menu_recent_history)
        setImageButtonEnabled(context!!, false, cancel_sleep_button, android.R.drawable.ic_menu_close_clear_cancel)
        if (currentPlayerServiceIntent?.extras?.getParcelable<RadioStation>(MediaPlayerService.RADIO_STATION_KEY) == radioStation) {
            invertEnabled()
        }
    }

    private fun setEventListeners() {
        play_button.setOnClickListener { playMedia(); invertEnabled() }
        pause_button.setOnClickListener { pauseMedia(); invertEnabled() }
        sleep_button.setOnClickListener { onScheduleStopButton() }
        stop_time.setOnClickListener {
            TimePickerFragment().setTimeEditText(stop_time).show(activity?.fragmentManager, "timePicker")
        }
        cancel_sleep_button.setOnClickListener { cancelScheduledStopService() }
    }

    private fun onScheduleStopButton() {
        if (stop_time.text.isNullOrEmpty()) return
        val time = stop_time.text.split(":")
        val hour = time[0].toInt()
        val minutes = time[1].toInt()
        scheduleStopService(context!!, hour, minutes)
        displayMessageInSnackBar(
            String.format(
                resources.getText(R.string.timer_scheduled_prompt).toString(),
                stop_time.text
            )
        )
        setImageButtonEnabled(context!!, true, cancel_sleep_button, android.R.drawable.ic_menu_close_clear_cancel)
    }

    private fun displayMessageInSnackBar(msg: String) {
        Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    private fun invertEnabled() {
        setImageButtonEnabled(context!!, !play_button.isEnabled, play_button, android.R.drawable.ic_media_play)
        setImageButtonEnabled(context!!, !pause_button.isEnabled, pause_button, android.R.drawable.ic_media_pause)
        setImageButtonEnabled(
            context!!,
            !sleep_button.isEnabled,
            sleep_button,
            android.R.drawable.ic_menu_recent_history
        )
        stop_time.isEnabled = !stop_time.isEnabled
    }

    private fun playMedia() {
        pauseMedia()
        val playerServiceIntent = Intent(activity, MediaPlayerService::class.java)
        playerServiceIntent.putExtra(MediaPlayerService.RADIO_STATION_KEY, radioStation)
        playerServiceIntent.action = MediaPlayerService.ACTION_PLAY
        activity?.startService(playerServiceIntent)
        isPlaying = true
        listener?.onPlayPlayerFragmentInteraction(playerServiceIntent)
    }

    private fun pauseMedia() {
        if (currentPlayerServiceIntent != null) activity?.stopService(currentPlayerServiceIntent)
        if (!isPlaying) return
        val playerIntent = Intent(activity, MediaPlayerService::class.java)
        playerIntent.action = MediaPlayerService.ACTION_STOP
        activity?.startService(playerIntent)
        cancelScheduledStopService(false)
        isPlaying = false
    }

    private fun cancelScheduledStopService(displayPrompt: Boolean = true) {
        if (scheduledStopServiceIntent != null) {
            alarmManager.cancel(scheduledStopServiceIntent)
        }
        if (displayPrompt) {
            displayMessageInSnackBar(resources.getText(R.string.timer_cancelled_prompt).toString())
        }
        stop_time.text = String().toEditable()
        setImageButtonEnabled(context!!, false, cancel_sleep_button, android.R.drawable.ic_menu_close_clear_cancel)
    }


    private fun scheduleStopService(mContext: Context, hour: Int, minute: Int) {
        val intent = Intent(mContext, MediaPlayerService::class.java)
        intent.action = MediaPlayerService.ACTION_STOP
        scheduledStopServiceIntent = PendingIntent.getService(mContext, 0, intent, 0)

        // reset previous pending intent
        alarmManager.cancel(scheduledStopServiceIntent)

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
            scheduledStopServiceIntent
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
        fun onPlayPlayerFragmentInteraction(playerIntent: Intent)
    }

    private fun setImageButtonEnabled(
        context: Context, enabled: Boolean,
        item: ImageButton, iconResId: Int
    ) {
        item.isEnabled = enabled
        item.isClickable = enabled
        val originalIcon = context.resources.getDrawable(iconResId)
        val icon = if (enabled) originalIcon else convertDrawableToGrayScale(originalIcon)
        item.setImageDrawable(icon)
    }

    private fun convertDrawableToGrayScale(drawable: Drawable?): Drawable? {
        if (drawable == null) {
            return null
        }
        val res = drawable.mutate()
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        return res
    }

    companion object {
        const val RADIO_STATION = "com.radio.annwy.radio.station"
        const val PLAYER_SERVICE_INTENT = "com.radio.annwy.radio.player_service_intent"

        @JvmStatic
        fun newInstance(radioStation: RadioStation, playerIntent: Intent?) = RadioPlayer().apply {
            arguments = Bundle().apply {
                putParcelable(RADIO_STATION, radioStation)
                putParcelable(PLAYER_SERVICE_INTENT, playerIntent)
            }
        }

        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    }
}
