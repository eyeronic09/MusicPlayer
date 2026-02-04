package com.example.musicplayer.Utilts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val validExtensions = listOf("aac", "flac", "mp3", "ogg", "opus", "wav", "m4a")
    
    // Try to get extension from URI path first
    val uriPath = uri.path ?: ""
    val lastSegment = uri.lastPathSegment ?: ""
    
    // Extract extension from the filename
    val fileExtension = if (lastSegment.contains(".")) {
        lastSegment.substringAfterLast(".").lowercase()
    } else {
        // Fallback to MIME type method
        val fileType: String? = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
        fileType?.lowercase() ?: ""
    }
    return validExtensions.contains(fileExtension)
}

suspend fun getEmbeddedArtwork(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()

    try {
        Log.d("ArtworkDebug", "Attempting to load artwork from URI: $uri")
        retriever.setDataSource(context, uri)

        val artBytes = retriever.embeddedPicture
        Log.d("ArtworkDebug", "Artwork bytes: ${artBytes?.size ?: "null"} bytes")
        
        if (artBytes != null && artBytes.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
            Log.d("ArtworkDebug", "Bitmap decoded: ${bitmap?.width}x${bitmap?.height}")
            bitmap
        } else {
            Log.d("ArtworkDebug", "No embedded artwork found")
            null
        }

    } catch (e: Exception) {
        Log.e("ArtworkDebug", "Error loading artwork", e)
        null
    } finally {
        retriever.release()
    }
}