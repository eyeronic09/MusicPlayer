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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(songEntity: SongEntity)

    @Delete
    suspend fun deleteSong(songEntity: SongEntity)

    @Query("SELECT * FROM song_DB")

    fun getAllSong() : Flow<List<SongEntity>>

    @Query("Select  * from  song_DB Where songId = :songId")
    fun getSongFromTable(songId : Int): Flow<SongEntity?>

}