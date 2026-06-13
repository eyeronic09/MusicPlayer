package com.example.musicplayer.HomeScreen.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity

@Dao
interface SongDao {
    @Insert
    fun insertSong(songEntity: SongEntity)

    @Delete
    fun deleteDelete(delete: SongEntity)
}