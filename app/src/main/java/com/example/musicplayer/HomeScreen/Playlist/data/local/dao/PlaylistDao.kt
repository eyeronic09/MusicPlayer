package com.example.musicplayer.HomeScreen.Playlist.data.local.dao

import androidx.room.*
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlayListEntity
import com.example.musicplayer.HomeScreen.Playlist.data.local.entity.PlaylistSongCrossRef
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlayListEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlayListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlayListEntity>>

    @Transaction
    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    fun getSongsOfPlaylist(playlistId: Long): Flow<PlaylistWithSongs>
}


// this is associate entity
data class PlaylistWithSongs(
    @Embedded val playlist: PlayListEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<SongEntity>
)
