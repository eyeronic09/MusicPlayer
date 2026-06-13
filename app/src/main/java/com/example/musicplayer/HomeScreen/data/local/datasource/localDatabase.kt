package com.example.musicplayer.HomeScreen.data.local.datasource

import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity

interface localDatabase {
    fun getAllSongPlaylist()
    fun insertSong(songEntity: SongEntity)
    fun deleteSong(songEntity: SongEntity)
}