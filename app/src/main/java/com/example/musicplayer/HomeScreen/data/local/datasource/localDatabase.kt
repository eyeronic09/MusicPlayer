package com.example.musicplayer.HomeScreen.data.local.datasource

import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

interface localDatabase {
    fun getAllSongPlaylist(): Flow<List<SongEntity>>
    suspend fun insertSong(songEntity: SongEntity)
    suspend fun deleteSong(songEntity: SongEntity)
}