package com.example.musicplayer.MusicPlayerScreen.Service

import android.content.Intent
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayer.MusicPlayerScreen.notification.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.milliseconds

class JetAudioService : MediaSessionService() {

    private val exoPlayer: ExoPlayer by inject()
    private val notificationManager: NotificationManager by inject()
    
    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private fun shutdownAndDestroy() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
        stopSelf()
    }

    private fun startKillTimer(durationInMillisecond: Long) {
        stopKillTimer()
        timerJob = serviceScope.launch {
            delay(durationInMillisecond.milliseconds)
            shutdownAndDestroy()
        }
        Log.d("timerJob", timerJob?.isActive.toString())
    }

    private fun stopKillTimer() {
        timerJob?.cancel()
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "START_SLEEP_TIMER") {
            val duration = intent.getLongExtra("TIMER_DURATION_MS", 0L)
            if (duration > 0) {
                startKillTimer(duration)
                Log.d("timerJob" , duration.toString() )
            } else {
                stopKillTimer()
            }
        }

        mediaSession?.let {
            notificationManager.startNotificationService(
                mediaSession = it,
                mediaSessionService = this
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            // DO NOT release the singleton player here, just stop it
            if (player.playbackState != Player.STATE_IDLE) {
                player.stop()
                player.clearMediaItems()
            }
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // If music is not playing, stop the service
        if (!exoPlayer.playWhenReady || exoPlayer.playbackState == Player.STATE_IDLE) {
            stopSelf()
        }
    }
}
