package com.annwy.radio

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedMetaData
import android.net.wifi.WifiManager
import android.os.*
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.annwy.radio.models.RadioStation
import android.support.v4.media.app.NotificationCompat as MediaNotificationCompat
import java.io.IOException

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnTimedMetaDataAvailableListener {
    override fun onSeekComplete(p0: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTimedMetaDataAvailable(p0: MediaPlayer?, p1: TimedMetaData?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(p0: MediaPlayer?) {
        Log.e("MediaPlayerServiceError", "Something went wrong. Resetting...")
        initMediaPlayer(radioStation!!.radioUrl)
        wifiLock.acquire()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Log.e("MediaPlayerServiceError", "Something went wrong. Resetting...")
        initMediaPlayer(radioStation!!.radioUrl)
        wifiLock.acquire()
        return true
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.app_name)
            val descriptionText = applicationContext.resources.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID.toString(), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
        if (intent.hasExtra(RADIO_STATION_KEY)) {
            radioStation = intent.extras.getParcelable(RADIO_STATION_KEY)
        }
        when (action) {
            ACTION_PLAY -> {
                if (!mMediaPlayer.isPlaying) {
                    startPlayer()
                }
            }
            ACTION_PAUSE -> {
                if (mMediaPlayer.isPlaying) {
                    pauseRadio()
                }
            }
            ACTION_STOP -> {
                if (mMediaPlayer.isPlaying) {
                    stopRadio()
                }
            }
            ACTION_CLOSE -> stopSelf()
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
        createNotificationChannel()
        val pauseIntent = createPlayerIntent(ACTION_STOP)
        val playIntent = createPlayerIntent(ACTION_PLAY)
        val closeIntent = createPlayerIntent(ACTION_CLOSE)

        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID.toString())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_radio_black_24dp)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("Now playing: ${radioStation?.radioName}")
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(android.R.drawable.ic_media_play, "Play", playIntent)
            .addAction(android.R.drawable.ic_media_pause, "Pause", pauseIntent)
            .addAction(android.R.drawable.ic_notification_clear_all, "Close", closeIntent)
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(closeIntent)
                    .setShowActionsInCompactView(1)
            )

        return mBuilder.build()
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

    private fun createPlayerIntent(action: String): PendingIntent {
        val playerServiceIntent = Intent(this, MediaPlayerService::class.java)
        playerServiceIntent.action = action
        return PendingIntent.getService(this, SERVICE_FLAG, playerServiceIntent, 0)
    }

    companion object {
        const val ACTION_PLAY: String = "com.example.action.PLAY"
        const val ACTION_PAUSE: String = "com.example.action.PAUSE"
        const val ACTION_STOP: String = "com.example.action.STOP"
        const val ACTION_CLOSE: String = "com.example.action.CLOSE"

        const val RADIO_STATION_KEY: String = "com.example.keys.RADIO_STATION"
        private const val CHANNEL_ID = 997
        private const val SERVICE_FLAG = 976
    }
}