package com.example.musicplayer.MusicPlayerScreen.Service

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayer.MusicPlayerScreen.notification.NotificationManager
import org.koin.android.ext.android.inject

class JetAudioService : MediaSessionService() {

    private val exoPlayer: ExoPlayer by inject()
    private val notificationManager: NotificationManager by inject()
    
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSession?.let {
            notificationManager.startNotificationService(
                mediaSession = it,
                mediaSessionService = this
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            if(player.playbackState != Player.STATE_IDLE){
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

}