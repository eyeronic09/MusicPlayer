package com.example.musicplayer.Utilts

import android.annotation.SuppressLint
import kotlin.time.Duration

@SuppressLint("DefaultLocale")
fun formatTimeWithHours(timeMs: Float): String {
    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours.toInt(), minutes.toInt(), seconds.toInt())
    } else {
        String.format("%02d:%02d", minutes.toInt(), seconds.toInt())
    }
}

@SuppressLint("DefaultLocale")
fun DurationFormater(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
