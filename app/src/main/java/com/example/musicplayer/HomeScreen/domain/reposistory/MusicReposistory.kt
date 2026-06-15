package com.example.musicplayer.HomeScreen.domain.reposistory

import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getAllAudioFilesFromDb() : Flow<List<AudioFile>>

    suspend fun insertSongEntity(songEntity: SongEntity)

    suspend fun insertMediaStoreToDB()
}