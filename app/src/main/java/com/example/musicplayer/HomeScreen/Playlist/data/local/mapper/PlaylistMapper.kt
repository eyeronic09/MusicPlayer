package com.example.musicplayer.HomeScreen.Playlist.data.local.mapper

import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlayListEntity
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList

fun PlayListEntity.toDomain() : PlayList{
    return PlayList(
        playlistId = this.playlistId,
        playListName = this.playlistName
    )
}

fun PlayList.toEntity() : PlayListEntity{
    return PlayListEntity(
        playlistId = this.playlistId,
        playlistName = this.playListName
    )
}