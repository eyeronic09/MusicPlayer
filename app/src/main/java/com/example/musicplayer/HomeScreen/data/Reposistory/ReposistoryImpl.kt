package com.example.musicplayer.HomeScreen.data.Reposistory

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.invalidateGroupsWithKey
import com.example.musicplayer.HomeScreen.data.local.dao.SongDao
import com.example.musicplayer.HomeScreen.data.local.entity.SongEntity
import com.example.musicplayer.HomeScreen.data.local.mapper.toAudioFile
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReposistoryImpl(
    private val context: Context,
    private val songDao: SongDao

) : MusicRepository {
    override suspend fun insertMediaStoreToDB() {
        withContext(Dispatchers.IO) {
            Log.d("MusicRepository", "Fetching audio files...")
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM
            )
            val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            try {
                context.contentResolver.query(
                    queryUri,
                    projection,
                    null,
                    null,
                    "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)


                    // It is stored here primarily to fetch the album art (cover image) associated
                    val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val artist = cursor.getString(artistColumn)
                        val duration = cursor.getInt(durationColumn)
                        val albumId = cursor.getLong(albumIdColumn)
                        val album = cursor.getString(albumColumn)

                        songDao.insertSong(
                            songEntity = SongEntity(
                                songId = id,
                                albumIdForArt = albumId,
                                displayName = name,
                                artist = artist,
                                album = album,
                                duration = duration
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MusicRepository", "Error querying MediaStore", e)
            }
        }
    }


    override fun getAllAudioFilesFromDb(): Flow<List<AudioFile>> {
        return songDao.getAllSong().map { note -> note.map { it -> it.toAudioFile() }}
    }

    override suspend fun insertSongEntity(songEntity: SongEntity) {
        songDao.insertSong(songEntity)
    }


}
