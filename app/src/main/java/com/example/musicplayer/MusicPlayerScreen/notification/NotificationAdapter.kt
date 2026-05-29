package com.example.musicplayer.MusicPlayerScreen.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.Coil


@UnstableApi
class NotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {


    override fun getCurrentContentTitle(player: Player): CharSequence {
         return  player.mediaMetadata.albumTitle ?:"unknown"
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
       return pendingIntent
    }

    override fun getCurrentContentText(player: Player): CharSequence? {
       return player.mediaMetadata.displayTitle ?:"unknown"
    }

    override fun getCurrentLargeIcon(
        p0: Player,
        p1: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        return null
    }

}