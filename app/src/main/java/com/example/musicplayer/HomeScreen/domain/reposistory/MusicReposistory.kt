package com.example.musicplayer.HomeScreen.domain.reposistory

import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAudioFiles(): List<AudioFile>
}