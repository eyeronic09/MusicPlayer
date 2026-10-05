package com.example.musicplayer.localfiles.domain.Model

import android.provider.MediaStore.Audio

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val contentUri: String
)