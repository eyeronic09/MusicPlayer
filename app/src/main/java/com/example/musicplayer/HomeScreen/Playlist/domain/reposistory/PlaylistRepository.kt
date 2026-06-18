package com.example.musicplayer.HomeScreen.Playlist.domain.reposistory

import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlayListEntity
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository  {
    fun getAllPlayList () : Flow<List<PlayList>>

    suspend fun insertSongFromPlaylist(audioFile: AudioFile , playlist : Int)

    suspend fun insertPlaylist(playList: PlayList)
    suspend fun deletePlaylist(playList: PlayList)
}