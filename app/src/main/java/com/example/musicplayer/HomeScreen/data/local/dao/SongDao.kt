package com.example.musicplayer.HomeScreen.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE )
    suspend fun insertSong(songEntity: SongEntity)

    @Delete
    suspend fun deleteDelete(songEntity: SongEntity)

    @Query(value = "Select * FROM song_DB")
    fun getAllSong() : Flow<List<SongEntity>>

}