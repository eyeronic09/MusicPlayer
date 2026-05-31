package com.example.musicplayer.MusicPlayerScreen.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicplayer.HomeScreen.domain.model.AudioFile

fun AudioFile.toMediaItem() : MediaItem{
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(uri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(displayName)
                .setArtist(artist)
                .setAlbumTitle(album)
                .build()
        )
        .build()
}
