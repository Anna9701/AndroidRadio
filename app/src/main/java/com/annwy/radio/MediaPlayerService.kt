package com.annwy.radio

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.annwy.radio.models.RadioStation
import org.jetbrains.anko.activityManager
import java.io.IOException

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Log.e("MediaPlayerServiceError", "Something went wrong. Resetting...")
        mMediaPlayer.reset()
        return true
    }

    private val iBinder = Binder()
    private var mMediaPlayer = MediaPlayer()
    private lateinit var wifiLock: WifiManager.WifiLock
    private var radioStation: RadioStation? = null

    override fun onBind(p0: Intent?): IBinder {
        return iBinder
    }

    private fun initMediaPlayer(url: String) {
        mMediaPlayer = MediaPlayer()
        //Set up MediaPlayer event listeners
        mMediaPlayer.setOnPreparedListener(this@MediaPlayerService)
        //Reset so that the MediaPlayer is not pointing to another data source
        mMediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer.reset()

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            // Set the data source to the mediaFile location
            mMediaPlayer.setDataSource(url)
        } catch (ex: IOException) {
            ex.printStackTrace()
            stopSelf()
        }
        mMediaPlayer.prepareAsync()
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "radioLock")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action: String = intent.action
        when (action) {
            ACTION_PLAY -> {
                radioStation = intent.extras.getParcelable(RADIO_STATION_KEY)
                startPlayer()
            }
            ACTION_PAUSE -> pauseRadio()
            ACTION_STOP -> stopRadio()
        }
        return Service.START_REDELIVER_INTENT


    }

    /** Called when MediaPlayer is ready */
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mMediaPlayer.start()
    }

    private fun startPlayer() {
        val notification = createNotification()
        startForeground(1, notification)
        initMediaPlayer(radioStation!!.radioUrl)
        wifiLock.acquire()
    }

    private fun createNotification(): Notification {
        val builder = Notification.Builder(applicationContext)
        builder.setContentTitle("New Zealand Radio Player")
        builder.setContentText("Now playing: ${radioStation?.radioName}")
        return builder.build()
    }

    private fun pauseRadio() {
        mMediaPlayer.pause()
        wifiLock.release()
    }

    private fun stopRadio() {
        mMediaPlayer.stop()
        wifiLock.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
    }

    companion object {
        const val ACTION_PLAY: String = "com.example.action.PLAY"
        const val ACTION_PAUSE: String = "com.example.action.PAUSE"
        const val ACTION_STOP: String = "com.example.action.STOP"

        const val RADIO_STATION_KEY: String = "com.example.keys.RADIO_STATION"
    }
}