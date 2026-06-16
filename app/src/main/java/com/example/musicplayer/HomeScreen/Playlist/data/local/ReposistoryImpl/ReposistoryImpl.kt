package com.example.musicplayer.HomeScreen.Playlist.data.local.ReposistoryImpl

import com.example.musicplayer.HomeScreen.Playlist.data.local.dao.PlaylistDao
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlayListEntity
import com.example.musicplayer.HomeScreen.Playlist.data.local.mapper.toDomain
import com.example.musicplayer.HomeScreen.Playlist.data.local.mapper.toEntity
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReposistoryImpl (private  val dao: PlaylistDao) : PlaylistRepository {
    override fun getAllPlayList(): Flow<List<PlayList>> {
        return dao.getAllPlaylists().map { entities -> entities.map { it.toDomain() } }
    }


    override suspend fun insertPlaylist(playList: PlayList) {
        return dao.insertPlaylist(playList.toEntity())
    }

    override suspend fun deletePlaylist(playList: PlayList) {
        return dao.deletePlaylist(playList.toEntity())
    }
}