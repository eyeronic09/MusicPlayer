package com.example.musicplayer.HomeScreen.data.local.datasource

import com.example.musicplayer.HomeScreen.data.local.dao.SongDao
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity

class localDatabaseImpl (private val dao  : SongDao) : localDatabase{
    override fun getAllSongPlaylist() {
        return TODO()
    }

    override fun insertSong(songEntity: SongEntity) {
       return dao.insertSong(songEntity)
    }

    override fun deleteSong(songEntity: SongEntity) {
        return dao.deleteDelete(songEntity)
    }
}