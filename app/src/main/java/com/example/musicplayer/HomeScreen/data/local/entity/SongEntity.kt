package com.example.musicplayer.HomeScreen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long =  0 ,
    val albumIdForArt: Long,
    val displayName : String,
    val artist: String,
    val album: String,
    val duration: Int,
)