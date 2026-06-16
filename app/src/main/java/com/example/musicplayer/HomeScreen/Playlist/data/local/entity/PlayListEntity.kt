package com.example.musicplayer.HomeScreen.Playlist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long,
    val playlistName: String
)


// cross associative table
@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)
