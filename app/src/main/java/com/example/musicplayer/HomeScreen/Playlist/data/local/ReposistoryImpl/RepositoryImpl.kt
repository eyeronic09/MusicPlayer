package com.example.musicplayer.HomeScreen.Playlist.data.local.ReposistoryImpl

import com.example.musicplayer.HomeScreen.Playlist.data.local.dao.PlaylistDao
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlaylistSongCrossRef
import com.example.musicplayer.HomeScreen.Playlist.data.local.mapper.toDomain
import com.example.musicplayer.HomeScreen.Playlist.data.local.mapper.toEntity
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.data.local.mapper.toAudioFile
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryImpl (private  val dao: PlaylistDao) : PlaylistRepository {
    override fun getAllPlayList(): Flow<List<PlayList>> {
        return dao.getAllPlaylists().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getSongsInPlaylist(id: Long): Flow<List<AudioFile>> {
        return dao.getSongsInPlaylist(playlistId = id).map { songPlay -> songPlay.map { it -> it.toAudioFile() }   }
    }

    override suspend fun insertSongFromPlaylist(
        audioFile: AudioFile,
        playlist: Long
    ) {
        if (playlist <= -1L ) {
            print("not doing this")
        } else {
            val crossRef = PlaylistSongCrossRef(
                playlistId = playlist,
                songId = audioFile.id
            )
            dao.insertPlaylistSongCrossRef(crossRef)
        }

    }


    override suspend fun insertPlaylist(playList: PlayList) {
        dao.insertPlaylist(playList.toEntity())
    }

    override suspend fun deletePlaylist(playList: PlayList) {
        dao.deletePlaylist(playList.toEntity())
    }
}