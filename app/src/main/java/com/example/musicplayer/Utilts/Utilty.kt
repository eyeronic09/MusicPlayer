package com.example.musicplayer.Utilts

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

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
fun formatDuration(timeMs: Long): String {
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

fun isValidAudioExtension(context: Context, uri: Uri): Boolean {
    val validExtensions = listOf("aac", "flac", "mp3", "ogg", "opus", "wav")
    val fileType: String? = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    val fileExtension = fileType?.lowercase() ?: ""
    return validExtensions.contains(fileExtension)
}