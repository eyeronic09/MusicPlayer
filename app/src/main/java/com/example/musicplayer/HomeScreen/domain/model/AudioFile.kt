package com.example.musicplayer.HomeScreen.domain.model

data class AudioFile(
    val id: Long,
    val displayName: String,
    val artist: String,
    val duration: Int,
)
