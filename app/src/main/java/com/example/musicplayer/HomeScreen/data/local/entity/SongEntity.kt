package com.example.musicplayer.HomeScreen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("song_DB")
data class SongEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Long ,
    val albumIdForArt: Long,
    val displayName : String,
    val artist: String,
    val album: String,
    val duration: Int,
)