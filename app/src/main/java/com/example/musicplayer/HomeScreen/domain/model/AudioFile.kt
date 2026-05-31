package com.example.musicplayer.HomeScreen.domain.model

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val id: Long,
    val displayName: String,
    val artist: String,
    val album: String,
    val duration: Int,
) : Parcelable {
    @IgnoredOnParcel
    val uri: Uri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    )
}