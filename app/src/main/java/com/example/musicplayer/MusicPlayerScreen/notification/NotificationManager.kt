package com.example.musicplayer.MusicPlayerScreen.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.session.MediaSession
import android.os.Build
import androidx.annotation.OptIn
import androidx.appcompat.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager

private const val Notification_ID = 101
private const val Notification_Channel = "Notification_channel_1"
private const val Notification_Channel_ID = "Notification_channel_ID_1"


class NotificationManager (private val context : Context, private val exoPlayer: ExoPlayer){
    private val notificationManager : NotificationManagerCompat =
        NotificationManagerCompat.from(context)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Notification_Channel_ID,
                Notification_Channel,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    init {
        createNotificationChannel()
    }

    @OptIn(UnstableApi::class)
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: androidx.media3.session.MediaSession
    ) {
        val notification = NotificationCompat.Builder(context, Notification_Channel_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .build()
        mediaSessionService.startForeground(Notification_ID, notification)

        buildNotification(mediaSession)
        startFN(mediaSessionService)

    }

    private fun startFN(mediaSessionService: MediaSessionService ){
        val notification = NotificationCompat.Builder(context, Notification_Channel_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        mediaSessionService.startForeground(Notification_ID, notification)
    }
    @OptIn(UnstableApi::class)
    @UnstableApi
    private fun buildNotification(mediaSession: androidx.media3.session.MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            Notification_ID,
            Notification_Channel_ID
        )
            .setMediaDescriptionAdapter(
                NotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.platformToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(exoPlayer)
            }
    }
}