package com.example.musicplayer.HomeScreen.data.local.mapper

import com.example.musicplayer.HomeScreen.compontent.AudioItem
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import com.example.musicplayer.HomeScreen.domain.model.AudioFile

fun AudioFile_to_SongEntity(AudioFiles : AudioFile) : SongEntity{
    return SongEntity(
        id = AudioFiles.id,
        albumIdForArt = AudioFiles.albumIdForArt,
        displayName = AudioFiles.displayName,
        artist =  AudioFiles.artist,
        album = AudioFiles.album,
        duration = AudioFiles.duration
    )
}