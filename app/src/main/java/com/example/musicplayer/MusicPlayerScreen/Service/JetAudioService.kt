package com.example.musicplayer.MusicPlayerScreen.Service

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import com.example.musicplayer.MusicPlayerScreen.notification.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.getScopeId
import kotlin.time.Duration.Companion.milliseconds

class JetAudioService : MediaSessionService() {

    private val exoPlayer: ExoPlayer by inject()
    private val notificationManager: NotificationManager by inject()

    private var stopAtEndOfSong = false
    private var mediaSession: MediaSession? = null
    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var timerJob: Job? = null
    private var currentMediaItem = mediaSession?.player?.currentMediaItem


    private fun shutdownAndDestroyAtEndTimer() {
        Log.d("JetAudioService", "shutdownAndDestroyAtEndTimer called")
        stopSelf()
    }

    private val player = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Log.d("JetAudioService", "onMediaItemTransition: stopAtEndOfSong = $stopAtEndOfSong, reason = $reason")
            if(stopAtEndOfSong){
                shutdownAndDestroyAtEndTimer()
                stopAtEndOfSong = false
            }

        }
    }

    private fun startKillTimer(durationInMillisecond: Long) {
        Log.d("JetAudioService", "startKillTimer: duration = $durationInMillisecond ms")
        stopKillTimer()
        
        if (serviceJob.isCancelled) {
            serviceJob = SupervisorJob()
            serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
        }

        timerJob = serviceScope.launch(Dispatchers.Default) {
            Log.d("JetAudioService", "Timer Job started. isActive: $isActive")
            delay(durationInMillisecond.milliseconds)
            Log.d("JetAudioService", "Timer reached. Executing shutdown.")
            shutdownAndDestroyAtEndTimer()
        }
        Log.d("JetAudioService", "Timer Job scheduled. isActive: ${timerJob?.isActive}")
    }

    private fun stopKillTimer() {
        Log.d("JetAudioService", "stopKillTimer called")
        timerJob?.cancel()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("JetAudioService", "Service Created")
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        exoPlayer.addListener(player)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("JetAudioService", "onStartCommand: action = $action")

        if (action == "START_SLEEP_TIMER") {
            val duration = intent?.getLongExtra("TIMER_DURATION_MS", 0L) ?: 0L
            Log.d("JetAudioService", "Sleep Timer Intent received: duration = ${duration.milliseconds}")
            if (duration > 0) {
                startKillTimer(duration)
            } else {
                stopKillTimer()
            }
        } else if (action == "END_SONG_TIMER") {
            stopAtEndOfSong = true
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
        serviceJob.cancel()
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
