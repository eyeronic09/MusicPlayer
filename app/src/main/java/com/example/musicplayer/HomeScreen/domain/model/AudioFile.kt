package com.example.musicplayer.HomeScreen.domain.model

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

data class AudioFile(
    val id: Long,
    val displayName: String,
    val artist: String,
    val album: String,
    val duration: Int,
) {
    val uri: Uri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    )
}