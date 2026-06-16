package com.example.musicplayer.HomeScreen.data.local.mapper

import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import com.example.musicplayer.HomeScreen.domain.model.AudioFile

fun SongEntity.toAudioFile(): AudioFile {
    return AudioFile(
        id = this.id,
        displayName = this.displayName,
        artist = this.artist,
        album = this.album,
        duration = this.duration,
        albumIdForArt = this.albumIdForArt
    )
}


fun AudioFile.toEntity(): SongEntity {
    return SongEntity(
        songId = this.id,
        displayName = this.displayName,
        artist = this.artist,
        album = this.album,
        duration = this.duration,
        albumIdForArt = this.albumIdForArt
    )
}